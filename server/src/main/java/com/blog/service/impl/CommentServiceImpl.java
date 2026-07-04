package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.common.Result;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.mapper.CommentMapper;
import com.blog.service.CommentService;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    /**
     * 获取文章评论列表（包含完整子树，前端直接渲染）
     * 策略：先查出该文章所有顶级评论（分页），再查出它们的所有子评论，构建完整树
     */
    @Override
    public Result<List<Comment>> getCommentList(Long articleId) {
        // 1. 查出该文章所有顶级评论（按时间倒序）
        LambdaQueryWrapper<Comment> rootWrapper = new LambdaQueryWrapper<>();
        rootWrapper.eq(Comment::getArticleId, articleId);
        // parentId 为 null 或 0 都视为顶级
        rootWrapper.and(w -> w.eq(Comment::getParentId, 0L).or().isNull(Comment::getParentId));
        rootWrapper.orderByDesc(Comment::getCreateTime);
        List<Comment> roots = this.list(rootWrapper);

        if (roots.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        // 2. 一次性查出所有子评论（不限层级）
        LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Comment::getArticleId, articleId);
        childWrapper.isNotNull(Comment::getParentId).ne(Comment::getParentId, 0L);
        childWrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> allChildren = this.list(childWrapper);

        // 4. 构建树：{ parentId -> children }
        Map<Long, List<Comment>> childMap = new HashMap<>();
        for (Comment child : allChildren) {
            Long pid = child.getParentId();
            if (pid == null) pid = 0L;
            childMap.computeIfAbsent(pid, k -> new ArrayList<>()).add(child);
        }

        // 5. 填充 children 并填充用户信息
        fillUserInfo(roots);
        fillUserInfo(allChildren);

        for (Comment root : roots) {
            root.setChildren(buildTree(root.getId(), childMap));
        }

        return Result.success(roots);
    }

    /**
     * 递归构建子树
     */
    private List<Comment> buildTree(Long parentId, Map<Long, List<Comment>> childMap) {
        List<Comment> children = childMap.getOrDefault(parentId, Collections.emptyList());
        for (Comment child : children) {
            child.setChildren(buildTree(child.getId(), childMap));
        }
        return children;
    }

    /**
     * 填充用户信息（昵称、头像）
     */
    private void fillUserInfo(List<Comment> comments) {
        if (comments.isEmpty()) return;

        // 收集所有有 userId 的评论
        Set<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!userIds.isEmpty()) {
            Map<Long, User> userMap = userService.listByIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));

            for (Comment comment : comments) {
                if (comment.getUserId() != null) {
                    User user = userMap.get(comment.getUserId());
                    if (user != null) {
                        comment.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
                        comment.setUserAvatar(user.getAvatar());
                        // 标记博主（username 为 admin）
                        if ("admin".equals(user.getUsername())) {
                            comment.setIsAdmin(true);
                        }
                    }
                }
                // 游客：nickname 兜底
                if (comment.getNickname() == null || comment.getNickname().isBlank()) {
                    comment.setNickname(comment.getUsername() != null ? comment.getUsername() : "匿名用户");
                }
            }
        } else {
            // 无用户信息，全部当游客
            for (Comment comment : comments) {
                if (comment.getNickname() == null || comment.getNickname().isBlank()) {
                    comment.setNickname(comment.getUsername() != null ? comment.getUsername() : "匿名用户");
                }
            }
        }
    }

    @Override
    public Result<Comment> addComment(Long userId, Comment comment) {
        // 顶级评论 parentId 为 0，子评论 parentId 为具体 id
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }
        comment.setCreateTime(LocalDateTime.now());

        if (userId != null) {
            // 登录用户评论
            comment.setUserId(userId);
            User user = userService.getById(userId);
            if (user != null) {
                comment.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
                comment.setUserAvatar(user.getAvatar());
                if ("admin".equals(user.getUsername())) {
                    comment.setIsAdmin(true);
                }
            }
        } else {
            // 游客评论（昵称必填）
            comment.setUserId(null);
            if (comment.getNickname() == null || comment.getNickname().isBlank()) {
                return Result.error("请填写昵称");
            }
            comment.setUsername(comment.getNickname());
        }

        this.save(comment);
        return Result.success("评论成功", comment);
    }

    @Override
    public Result<Void> deleteComment(Long userId, Long commentId) {
        Comment comment = this.getById(commentId);
        if (comment == null) {
            return Result.error("评论不存在");
        }

        if (userId == null) {
            return Result.error("请先登录");
        }

        User user = userService.getById(userId);
        if (user != null && "admin".equals(user.getUsername())) {
            // 博主删除：只删除这一条，子评论的parentId改为被删除评论的parentId
            // 先找出所有子评论
            LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(Comment::getParentId, commentId);
            List<Comment> children = this.list(childWrapper);
            
            // 将子评论的parentId改为当前评论的parentId（提升一级）
            if (!children.isEmpty()) {
                Long newParentId = comment.getParentId() != null ? comment.getParentId() : 0L;
                for (Comment child : children) {
                    child.setParentId(newParentId);
                }
                this.updateBatchById(children);
            }
            
            // 删除当前评论
            this.removeById(commentId);
            return Result.success();
        }

        if (!java.util.Objects.equals(comment.getUserId(), userId)) {
            return Result.error("无权删除此评论");
        }

        // 普通用户删除同理
        LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Comment::getParentId, commentId);
        List<Comment> children = this.list(childWrapper);
        
        if (!children.isEmpty()) {
            Long newParentId = comment.getParentId() != null ? comment.getParentId() : 0L;
            for (Comment child : children) {
                child.setParentId(newParentId);
            }
            this.updateBatchById(children);
        }
        
        this.removeById(commentId);
        return Result.success();
    }

    /**
     * 递归删除子评论（保留方法，但不再使用）
     */
    private void removeChildren(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId);
        List<Comment> children = this.list(wrapper);
        for (Comment child : children) {
            removeChildren(child.getId());
            this.removeById(child.getId());
        }
    }
}

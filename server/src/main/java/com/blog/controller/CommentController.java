package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Comment;
import com.blog.service.CommentService;
import com.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取文章评论列表（树形结构）
     */
    @GetMapping("/list/{articleId}")
    public Result<List<Comment>> getCommentList(@PathVariable Long articleId) {
        return commentService.getCommentList(articleId);
    }

    /**
     * 发表评论
     */
    @PostMapping("/add")
    public Result<Comment> addComment(HttpServletRequest request, @RequestBody Comment comment) {
        Long userId = getUserIdFromRequest(request);
        return commentService.addComment(userId, comment);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteComment(HttpServletRequest request, @PathVariable Long id) {
        Long userId = getUserIdFromRequest(request);
        return commentService.deleteComment(userId, id);
    }

    /**
     * 从请求中获取用户ID（支持游客评论，返回null）
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isBlank()) return null;
        try {
            return jwtUtil.getUserId(token);
        } catch (Exception e) {
            return null;
        }
    }
}

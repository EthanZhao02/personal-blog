package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.common.Result;
import com.blog.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {
    
    /**
     * 获取文章评论列表（树形结构，一次性返回）
     */
    Result<List<Comment>> getCommentList(Long articleId);
    
    /**
     * 发表评论
     */
    Result<Comment> addComment(Long userId, Comment comment);
    
    /**
     * 删除评论
     */
    Result<Void> deleteComment(Long userId, Long commentId);
}

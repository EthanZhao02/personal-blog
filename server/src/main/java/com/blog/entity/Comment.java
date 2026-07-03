package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@TableName("test.blog_comment")
public class Comment implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String content;
    
    private Long articleId;
    
    private Long userId;
    
    private Long parentId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String userAvatar;

    /** 留言者昵称（游客/匿名评论） */
    private String nickname;

    /** 留言者邮箱 */
    private String email;

    /** 留言者网站 */
    private String website;

    /** 子评论列表（树形，非数据库字段） */
    @TableField(exist = false)
    private java.util.List<Comment> children;

    /** 是否博主（非数据库字段） */
    @TableField(exist = false)
    private Boolean isAdmin;
}

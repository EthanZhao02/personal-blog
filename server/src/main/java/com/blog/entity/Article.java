package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章实体类
 */
@Data
@TableName("test.blog_article")
public class Article implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String content;
    
    private String summary;
    
    private String coverImage;
    
    private Long categoryId;
    
    private Long userId;
    
    private Integer viewCount;
    
    private Integer isPublished;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private String categoryName;
    
    @TableField(exist = false)
    private String authorName;

    /**
     * 附件列表 JSON 字符串
     */
    private String attachments;
}

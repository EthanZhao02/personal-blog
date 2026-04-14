package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章标签关联实体
 */
@Data
@TableName("article_tag")
public class ArticleTag implements Serializable {
    
    private Long articleId;
    
    private Long tagId;
}

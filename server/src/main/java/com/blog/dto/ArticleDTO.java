package com.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 文章DTO
 */
@Data
public class ArticleDTO {
    
    private Long id;
    
    @NotBlank(message = "文章标题不能为空")
    private String title;
    
    @NotBlank(message = "文章内容不能为空")
    private String content;
    
    private String summary;
    
    private String coverImage;
    
    private Long categoryId;
    
    private List<Long> tagIds;
    
    private Integer isPublished;

    /**
     * 附件列表（JSON 字符串或对象）
     */
    private String attachments;
}

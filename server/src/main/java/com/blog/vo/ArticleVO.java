package com.blog.vo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 文章视图对象
 */
@Data
public class ArticleVO implements Serializable {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private Long userId;
    private String authorName;
    private Integer viewCount;
    private Integer isPublished;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<TagVO> tags;

    /**
     * 附件 JSON 字符串（数据库存储格式）
     */
    private String attachments;

    /**
     * 供前端使用的附件列表（自动从 JSON 反序列化）
     */
    public List<AttachmentVO> getAttachmentsList() {
        if (attachments == null || attachments.isEmpty()) return Collections.emptyList();
        try {
            return objectMapper.readValue(attachments, new TypeReference<List<AttachmentVO>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

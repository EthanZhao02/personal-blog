package com.blog.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章附件视图对象
 */
@Data
public class AttachmentVO implements Serializable {
    private String name;
    private String url;
    private Long size;
}

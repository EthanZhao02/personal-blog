package com.blog.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 标签视图对象
 */
@Data
public class TagVO implements Serializable {
    private Long id;
    private String name;
}

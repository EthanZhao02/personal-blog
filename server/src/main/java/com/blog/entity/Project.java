package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("project")
public class Project implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String url;
    private String githubUrl;
    private String icon;
    private String imageUrl;
    private String tag;
    private String techStack;
    private String badge;
    private String category;
    private String stats;
    private String status;
    private Integer isOpenSource;
    private String articleUrl;
    private Integer sortOrder;
    private Integer isActive;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

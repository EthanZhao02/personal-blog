package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("test.blog_message")
public class Message implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nickname;
    private String email;
    private String website;
    private String content;
    private Long parentId;
    private String reply;
    private String avatar;
    private String ipAddress;
    private Integer isShow;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 非DB字段：回复列表 */
    @TableField(exist = false)
    private java.util.List<Message> children;
}

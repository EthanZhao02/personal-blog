package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("test.blog_profile")
public class Profile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String tagline;
    private String bio;
    private String location;
    private String status;
    private String avatar;
    /** JSON 数组: [{"name":"Vue","level":90},...] */
    private String skills;
    /** JSON 数组: [{"name":"GitHub","icon":"github","url":"...","color":"#...","handle":"..."},...] */
    private String socials;
}

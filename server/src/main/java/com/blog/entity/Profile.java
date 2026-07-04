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
    /** JSON 数组: ["AI","Web Dev","Open Source",...] */
    private String interests;
    /** JSON 数组: [{"name":"GitHub","icon":"github","url":"...","color":"#...","handle":"..."},...] */
    private String socials;
    /** JSON 数组: ["阅读","摄影","...",...] */
    private String hobbies;
    /** JSON 数组: [{"name":"VS Code","category":"编辑器"},...] */
    private String tools;
    /** 博客历程文本 */
    private String blogStory;
    /** 名字由来文本 */
    private String nameOrigin;
}

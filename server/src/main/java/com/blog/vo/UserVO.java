package com.blog.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户视图对象
 */
@Data
public class UserVO implements Serializable {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private Boolean isAdmin;
    private String token;
}

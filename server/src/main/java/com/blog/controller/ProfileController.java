package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Profile;
import com.blog.entity.User;
import com.blog.service.ProfileService;
import com.blog.service.UserService;
import com.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /** 获取个人资料（公开） */
    @GetMapping
    public Result<Profile> get() {
        Profile profile = profileService.getById(1L);
        return Result.success(profile);
    }

    /** 更新个人资料（管理员） */
    @PutMapping
    public Result<Void> update(HttpServletRequest request, @RequestBody Profile profile) {
        Long userId = getUserId(request);
        if (userId == null) return Result.error("请先登录");
        User user = userService.getById(userId);
        if (user == null || !"admin".equals(user.getUsername())) {
            return Result.error("无权操作");
        }
        profile.setId(1L);
        profileService.saveOrUpdate(profile);
        return Result.success();
    }

    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        if (token == null || token.isBlank()) return null;
        try { return jwtUtil.getUserId(token); } catch (Exception e) { return null; }
    }
}

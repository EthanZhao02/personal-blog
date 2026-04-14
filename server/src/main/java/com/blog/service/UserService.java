package com.blog.service;

import com.blog.common.Result;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    Result<UserVO> register(RegisterDTO registerDTO);
    
    /**
     * 用户登录
     */
    Result<UserVO> login(LoginDTO loginDTO);
    
    /**
     * 获取用户信息
     */
    Result<UserVO> getUserInfo(Long userId);
    
    /**
     * 更新用户信息
     */
    Result<UserVO> updateUserInfo(Long userId, User user);
    
    /**
     * 更新头像
     */
    Result<String> updateAvatar(Long userId, String avatarUrl);
}

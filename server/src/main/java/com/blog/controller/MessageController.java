package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Message;
import com.blog.entity.User;
import com.blog.service.MessageService;
import com.blog.service.UserService;
import com.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<Message>> getList() {
        List<Message> list = messageService.getPublicMessages();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<Message> addMessage(HttpServletRequest request, @RequestBody Message message) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return Result.error("留言内容不能为空");
        }
        if (message.getContent().length() > 500) {
            return Result.error("留言内容不能超过500字");
        }
        String nickname = message.getNickname();
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = "匿名用户";
        }
        message.setNickname(nickname.trim().substring(0, Math.min(50, nickname.trim().length())));
        String ip = getClientIp(request);
        Message saved = messageService.addMessage(message, ip);
        return Result.success("留言成功", saved);
    }

    /** 管理员回复留言 */
    @PostMapping("/reply/{id}")
    public Result<Message> replyMessage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        if (reply == null || reply.trim().isEmpty()) {
            return Result.error("回复内容不能为空");
        }
        Message msg = messageService.replyMessage(id, reply.trim());
        if (msg == null) return Result.error("留言不存在");
        return Result.success("回复成功", msg);
    }

    /** 删除留言（管理员） */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Long id) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return Result.error("请先登录");
        }
        User user = userService.getById(userId);
        if (user == null || !"admin".equals(user.getUsername())) {
            return Result.error("无权删除");
        }
        boolean ok = messageService.deleteMessage(id);
        return ok ? Result.success() : Result.error("删除失败");
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isBlank()) return null;
        try {
            return jwtUtil.getUserId(token);
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

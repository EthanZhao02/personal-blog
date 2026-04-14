package com.blog.service;

import com.blog.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface MessageService extends IService<Message> {
    List<Message> getPublicMessages();
    Message addMessage(Message message, String ip);
    boolean deleteMessage(Long id);
    Message replyMessage(Long id, String replyContent);
}

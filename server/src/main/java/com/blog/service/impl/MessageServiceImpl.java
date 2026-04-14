package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Message;
import com.blog.mapper.MessageMapper;
import com.blog.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public List<Message> getPublicMessages() {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getIsShow, 1);
        wrapper.orderByDesc(Message::getCreateTime);
        List<Message> all = this.list(wrapper);

        // 构建树形结构
        Map<Long, Message> map = new LinkedHashMap<>();
        List<Message> roots = new ArrayList<>();
        for (Message m : all) {
            m.setChildren(new ArrayList<>());
            map.put(m.getId(), m);
        }
        for (Message m : all) {
            if (m.getParentId() != null && m.getParentId() > 0 && map.containsKey(m.getParentId())) {
                map.get(m.getParentId()).getChildren().add(m);
            } else {
                roots.add(m);
            }
        }
        return roots;
    }

    @Override
    public Message addMessage(Message message, String ip) {
        message.setIsShow(1);
        message.setCreateTime(LocalDateTime.now());
        message.setParentId(message.getParentId() != null ? message.getParentId() : 0L);
        if (ip != null && ip.length() > 50) ip = ip.substring(0, 50);
        message.setIpAddress(ip != null ? ip : "");
        this.save(message);
        return message;
    }

    @Override
    public boolean deleteMessage(Long id) {
        // 只删除这一条，子留言提升一级
        Message msg = this.getById(id);
        if (msg == null) return false;
        
        // 找出所有子留言
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getParentId, id);
        List<Message> children = this.list(wrapper);
        
        // 将子留言的parentId改为当前留言的parentId
        if (!children.isEmpty()) {
            Long newParentId = msg.getParentId() != null ? msg.getParentId() : 0L;
            for (Message child : children) {
                child.setParentId(newParentId);
            }
            this.updateBatchById(children);
        }
        
        return this.removeById(id);
    }

    /** 管理员回复留言（创建新的子留言） */
    @Override
    public Message replyMessage(Long id, String replyContent) {
        Message parent = this.getById(id);
        if (parent == null) return null;
        
        // 创建新的子留言，而不是覆盖reply字段
        Message reply = new Message();
        reply.setNickname("博主");
        reply.setContent(replyContent);
        reply.setIsShow(1);
        reply.setCreateTime(LocalDateTime.now());
        reply.setParentId(id);
        reply.setIpAddress("");
        this.save(reply);
        
        return reply;
    }
}

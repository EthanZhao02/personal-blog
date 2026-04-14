package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.FriendLink;
import com.blog.mapper.FriendLinkMapper;
import com.blog.service.FriendLinkService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {

    @Override
    public List<FriendLink> getActiveLinks() {
        LambdaQueryWrapper<FriendLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendLink::getIsActive, 1);
        wrapper.orderByAsc(FriendLink::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    public FriendLink addLink(FriendLink link) {
        link.setCreateTime(LocalDateTime.now());
        link.setUpdateTime(LocalDateTime.now());
        this.save(link);
        return link;
    }

    @Override
    public boolean deleteLink(Long id) {
        return this.removeById(id);
    }

    @Override
    public FriendLink updateLink(FriendLink link) {
        link.setUpdateTime(LocalDateTime.now());
        this.updateById(link);
        return link;
    }
}

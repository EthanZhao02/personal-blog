package com.blog.service;

import com.blog.entity.FriendLink;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface FriendLinkService extends IService<FriendLink> {
    List<FriendLink> getActiveLinks();
    FriendLink addLink(FriendLink link);
    boolean deleteLink(Long id);
    FriendLink updateLink(FriendLink link);
}

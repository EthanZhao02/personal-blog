package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.common.Result;
import com.blog.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService extends IService<Tag> {
    
    /**
     * 获取所有标签
     */
    Result<List<Tag>> getTagList();
    
    /**
     * 添加标签
     */
    Result<Tag> addTag(Tag tag);
    
    /**
     * 更新标签
     */
    Result<Tag> updateTag(Tag tag);
    
    /**
     * 删除标签
     */
    Result<Void> deleteTag(Long id);
}

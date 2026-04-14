package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.common.Result;
import com.blog.entity.ArticleTag;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public Result<List<Tag>> getTagList() {
        List<Tag> tags = this.list();
        return Result.success(tags);
    }

    @Override
    public Result<Tag> addTag(Tag tag) {
        // 检查标签名是否存在
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tag.getName());
        if (this.count(wrapper) > 0) {
            return Result.error("标签名已存在");
        }

        tag.setCreateTime(LocalDateTime.now());
        this.save(tag);
        return Result.success("添加成功", tag);
    }

    @Override
    public Result<Tag> updateTag(Tag tag) {
        Tag existTag = this.getById(tag.getId());
        if (existTag == null) {
            return Result.error("标签不存在");
        }

        // 检查标签名是否重复
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tag.getName());
        wrapper.ne(Tag::getId, tag.getId());
        if (this.count(wrapper) > 0) {
            return Result.error("标签名已存在");
        }

        existTag.setName(tag.getName());
        this.updateById(existTag);
        return Result.success("更新成功", existTag);
    }

    @Override
    public Result<Void> deleteTag(Long id) {
        // 删除文章标签关联
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getTagId, id);
        articleTagMapper.delete(wrapper);

        this.removeById(id);
        return Result.success();
    }
}

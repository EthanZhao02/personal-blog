package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签
     */
    @GetMapping("/list")
    public Result<List<Tag>> getTagList() {
        return tagService.getTagList();
    }

    /**
     * 添加标签
     */
    @PostMapping("/add")
    public Result<Tag> addTag(@RequestBody Tag tag) {
        return tagService.addTag(tag);
    }

    /**
     * 更新标签
     */
    @PutMapping("/update")
    public Result<Tag> updateTag(@RequestBody Tag tag) {
        return tagService.updateTag(tag);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        return tagService.deleteTag(id);
    }
}

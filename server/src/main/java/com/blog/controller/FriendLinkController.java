package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.FriendLink;
import com.blog.service.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friend/link")
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    /** 获取已审核友链（公开） */
    @GetMapping("/list")
    public Result<List<FriendLink>> getList() {
        List<FriendLink> list = friendLinkService.getActiveLinks();
        return Result.success(list);
    }

    /** 获取全部友链（管理员，含待审核/已下架） */
    @GetMapping("/all")
    public Result<List<FriendLink>> getAll() {
        List<FriendLink> list = friendLinkService.list();
        return Result.success(list);
    }

    /** 申请友链（公开） */
    @PostMapping("/apply")
    public Result<String> apply(@RequestBody FriendLink link) {
        if (link.getName() == null || link.getName().trim().isEmpty()) {
            return Result.error("网站名称不能为空");
        }
        if (link.getUrl() == null || link.getUrl().trim().isEmpty()) {
            return Result.error("网站地址不能为空");
        }
        // 新申请默认待审核
        link.setIsActive(0);
        friendLinkService.addLink(link);
        return Result.success("申请已提交，待管理员审核");
    }

    /** 管理员直接添加友链（跳过审核） */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody FriendLink link) {
        if (link.getName() == null || link.getName().trim().isEmpty()) {
            return Result.error("网站名称不能为空");
        }
        if (link.getUrl() == null || link.getUrl().trim().isEmpty()) {
            return Result.error("网站地址不能为空");
        }
        link.setIsActive(1);
        friendLinkService.addLink(link);
        return Result.success();
    }

    /** 审核友链（通过/拒绝） */
    @PutMapping("/approve/{id}")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        FriendLink link = friendLinkService.getById(id);
        if (link == null) return Result.error("友链不存在");
        link.setIsActive(body.getOrDefault("isActive", 1));
        friendLinkService.updateLink(link);
        return Result.success();
    }

    /** 编辑友链 */
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody FriendLink link) {
        link.setId(id);
        friendLinkService.updateLink(link);
        return Result.success();
    }

    /** 删除友链 */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        friendLinkService.deleteLink(id);
        return Result.success();
    }
}

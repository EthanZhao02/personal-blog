package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Project;
import com.blog.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /** 获取已展示项目（公开） */
    @GetMapping("/list")
    public Result<List<Project>> getList() {
        List<Project> list = projectService.getActiveProjects();
        return Result.success(list);
    }

    /** 获取全部项目（管理员） */
    @GetMapping("/all")
    public Result<List<Project>> getAll() {
        List<Project> list = projectService.list();
        return Result.success(list);
    }

    /** 添加项目 */
    @PostMapping("/add")
    public Result<Project> add(@RequestBody Project project) {
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            return Result.error("项目名称不能为空");
        }
        if (project.getUrl() == null || project.getUrl().trim().isEmpty()) {
            return Result.error("项目地址不能为空");
        }
        if (project.getIsActive() == null) {
            project.setIsActive(1); // 默认上线
        }
        if (project.getSortOrder() == null) {
            project.setSortOrder(0);
        }
        Project saved = projectService.addProject(project);
        return Result.success(saved);
    }

    /** 编辑项目 */
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        projectService.updateProject(project);
        return Result.success();
    }

    /** 删除项目 */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }
}

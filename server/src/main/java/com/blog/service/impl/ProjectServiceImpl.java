package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Project;
import com.blog.mapper.ProjectMapper;
import com.blog.service.ProjectService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Override
    public List<Project> getActiveProjects() {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getIsActive, 1);
        wrapper.orderByAsc(Project::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    public Project addProject(Project project) {
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        this.save(project);
        return project;
    }

    @Override
    public boolean deleteProject(Long id) {
        return this.removeById(id);
    }

    @Override
    public Project updateProject(Project project) {
        project.setUpdateTime(LocalDateTime.now());
        this.updateById(project);
        return project;
    }
}

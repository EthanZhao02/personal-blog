package com.blog.service;

import com.blog.entity.Project;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface ProjectService extends IService<Project> {
    List<Project> getActiveProjects();
    Project addProject(Project project);
    boolean deleteProject(Long id);
    Project updateProject(Project project);
}

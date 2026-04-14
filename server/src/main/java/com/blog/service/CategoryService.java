package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.common.Result;
import com.blog.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {
    
    /**
     * 获取所有分类
     */
    Result<List<Category>> getCategoryList();
    
    /**
     * 添加分类
     */
    Result<Category> addCategory(Category category);
    
    /**
     * 更新分类
     */
    Result<Category> updateCategory(Category category);
    
    /**
     * 删除分类
     */
    Result<Void> deleteCategory(Long id);
}

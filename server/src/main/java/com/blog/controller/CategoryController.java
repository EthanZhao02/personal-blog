package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Category;
import com.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类
     */
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        return categoryService.getCategoryList();
    }

    /**
     * 添加分类
     */
    @PostMapping("/add")
    public Result<Category> addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    /**
     * 更新分类
     */
    @PutMapping("/update")
    public Result<Category> updateCategory(@RequestBody Category category) {
        return categoryService.updateCategory(category);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}

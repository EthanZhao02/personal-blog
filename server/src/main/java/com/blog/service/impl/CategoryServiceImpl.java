package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.common.Result;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.mapper.CategoryMapper;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private ArticleService articleService;

    @Override
    public Result<List<Category>> getCategoryList() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        List<Category> categories = this.list(wrapper);
        return Result.success(categories);
    }

    @Override
    public Result<Category> addCategory(Category category) {
        // 检查分类名是否存在
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName());
        if (this.count(wrapper) > 0) {
            return Result.error("分类名已存在");
        }

        category.setCreateTime(LocalDateTime.now());
        this.save(category);
        return Result.success("添加成功", category);
    }

    @Override
    public Result<Category> updateCategory(Category category) {
        Category existCategory = this.getById(category.getId());
        if (existCategory == null) {
            return Result.error("分类不存在");
        }

        // 检查分类名是否重复
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName());
        wrapper.ne(Category::getId, category.getId());
        if (this.count(wrapper) > 0) {
            return Result.error("分类名已存在");
        }

        existCategory.setName(category.getName());
        existCategory.setSort(category.getSort());
        this.updateById(existCategory);
        return Result.success("更新成功", existCategory);
    }

    @Override
    public Result<Void> deleteCategory(Long id) {
        // 检查是否有文章使用该分类
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getCategoryId, id);
        if (articleService.count(wrapper) > 0) {
            return Result.error("该分类下存在文章，无法删除");
        }

        this.removeById(id);
        return Result.success();
    }
}

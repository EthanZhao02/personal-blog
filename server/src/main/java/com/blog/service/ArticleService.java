package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.ArticleDTO;
import com.blog.entity.Article;
import com.blog.vo.ArticleVO;

/**
 * 文章服务接口
 */
public interface ArticleService extends IService<Article> {
    
    /**
     * 分页查询文章列表
     */
    Result<PageResult<ArticleVO>> getArticleList(Integer current, Integer size, 
                                                   Long categoryId, Long tagId, String keyword);
    
    /**
     * 获取文章详情
     */
    Result<ArticleVO> getArticleDetail(Long id);
    
    /**
     * 发布文章
     */
    Result<ArticleVO> publishArticle(Long userId, ArticleDTO articleDTO);
    
    /**
     * 更新文章
     */
    Result<ArticleVO> updateArticle(Long userId, ArticleDTO articleDTO);
    
    /**
     * 删除文章
     */
    Result<Void> deleteArticle(Long userId, Long articleId);

    /**
     * 获取当前用户的文章列表（包含未发布的）
     */
    Result<PageResult<ArticleVO>> getMyArticles(Long userId, Integer current, Integer size);
}

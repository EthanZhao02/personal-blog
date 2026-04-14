package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Article;
import com.blog.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章Mapper
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    
    /**
     * 分页查询文章列表（包含分类名和作者名）
     */
    IPage<ArticleVO> selectArticlePage(Page<ArticleVO> page, 
                                        @Param("categoryId") Long categoryId,
                                        @Param("tagId") Long tagId,
                                        @Param("keyword") String keyword);
    
    /**
     * 查询文章详情
     */
    ArticleVO selectArticleDetail(@Param("id") Long id);
}

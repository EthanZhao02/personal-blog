package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.ArticleDTO;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.entity.Category;
import com.blog.entity.User;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.service.ArticleService;
import com.blog.service.UserService;
import com.blog.vo.ArticleVO;
import com.blog.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserService userService;

    @Override
    public Result<PageResult<ArticleVO>> getArticleList(Integer current, Integer size,
                                                         Long categoryId, Long tagId, String keyword) {
        Page<ArticleVO> page = new Page<>(current, size);
        IPage<ArticleVO> articlePage = articleMapper.selectArticlePage(page, categoryId, tagId, keyword);

        // 为每篇文章查询标签
        for (ArticleVO vo : articlePage.getRecords()) {
            vo.setTags(getArticleTags(vo.getId()));
        }

        PageResult<ArticleVO> pageResult = new PageResult<>(
                articlePage.getRecords(),
                articlePage.getTotal(),
                articlePage.getSize(),
                articlePage.getCurrent()
        );

        return Result.success(pageResult);
    }

    @Override
    public Result<ArticleVO> getArticleDetail(Long id) {
        ArticleVO articleVO = articleMapper.selectArticleDetail(id);
        if (articleVO == null) {
            return Result.error("文章不存在");
        }

        // 增加浏览量
        Article article = this.getById(id);
        article.setViewCount(article.getViewCount() + 1);
        this.updateById(article);

        // 查询标签
        articleVO.setTags(getArticleTags(id));

        return Result.success(articleVO);
    }

    @Override
    @Transactional
    public Result<ArticleVO> publishArticle(Long userId, ArticleDTO articleDTO) {
        // 创建文章
        Article article = new Article();
        article.setTitle(articleDTO.getTitle());
        article.setContent(articleDTO.getContent());
        article.setSummary(articleDTO.getSummary() != null ? articleDTO.getSummary() : 
                          generateSummary(articleDTO.getContent()));
        article.setCoverImage(articleDTO.getCoverImage());
        article.setCategoryId(articleDTO.getCategoryId());
        article.setUserId(userId);
        article.setViewCount(0);
        article.setIsPublished(articleDTO.getIsPublished() != null ? articleDTO.getIsPublished() : 1);
        article.setAttachments(articleDTO.getAttachments());
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());

        this.save(article);

        // 保存标签关联
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            saveArticleTags(article.getId(), articleDTO.getTagIds());
        }

        // 返回文章详情
        return getArticleDetail(article.getId());
    }

    @Override
    @Transactional
    public Result<ArticleVO> updateArticle(Long userId, ArticleDTO articleDTO) {
        Article article = this.getById(articleDTO.getId());
        if (article == null) {
            return Result.error("文章不存在");
        }

        // 验证作者
        if (!article.getUserId().equals(userId)) {
            return Result.error("无权修改此文章");
        }

        // 更新文章
        article.setTitle(articleDTO.getTitle());
        article.setContent(articleDTO.getContent());
        article.setSummary(articleDTO.getSummary() != null ? articleDTO.getSummary() : 
                          generateSummary(articleDTO.getContent()));
        article.setCoverImage(articleDTO.getCoverImage());
        article.setCategoryId(articleDTO.getCategoryId());
        article.setAttachments(articleDTO.getAttachments());
        if (articleDTO.getIsPublished() != null) {
            article.setIsPublished(articleDTO.getIsPublished());
        }
        article.setUpdateTime(LocalDateTime.now());

        this.updateById(article);

        // 更新标签关联
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, article.getId());
        articleTagMapper.delete(wrapper);

        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            saveArticleTags(article.getId(), articleDTO.getTagIds());
        }

        return getArticleDetail(article.getId());
    }

    @Override
    @Transactional
    public Result<Void> deleteArticle(Long userId, Long articleId) {
        Article article = this.getById(articleId);
        if (article == null) {
            return Result.error("文章不存在");
        }

        // 验证作者
        if (!article.getUserId().equals(userId)) {
            return Result.error("无权删除此文章");
        }

        // 删除标签关联
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, articleId);
        articleTagMapper.delete(wrapper);

        // 删除文章
        this.removeById(articleId);

        return Result.success();
    }

    @Override
    public Result<PageResult<ArticleVO>> getMyArticles(Long userId, Integer current, Integer size) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getUserId, userId);
        wrapper.orderByDesc(Article::getCreateTime);
        
        Page<Article> page = new Page<>(current, size);
        IPage<Article> articlePage = this.page(page, wrapper);

        // 转换为VO
        PageResult<ArticleVO> pageResult = new PageResult<>();
        pageResult.setRecords(articlePage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        pageResult.setTotal(articlePage.getTotal());
        pageResult.setSize(articlePage.getSize());
        pageResult.setCurrent(articlePage.getCurrent());

        return Result.success(pageResult);
    }

    /**
     * 实体转换为VO
     */
    private ArticleVO convertToVO(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setContent(article.getContent());
        vo.setSummary(article.getSummary());
        vo.setCoverImage(article.getCoverImage());
        vo.setCategoryId(article.getCategoryId());
        vo.setUserId(article.getUserId());
        vo.setViewCount(article.getViewCount());
        vo.setIsPublished(article.getIsPublished());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());
        
        // 查询分类名
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        // 查询作者名
        if (article.getUserId() != null) {
            User user = userService.getById(article.getUserId());
            if (user != null) {
                vo.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            }
        }
        
        // 查询标签
        vo.setTags(getArticleTags(article.getId()));
        
        return vo;
    }

    /**
     * 获取文章标签列表
     */
    private List<TagVO> getArticleTags(Long articleId) {
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, articleId);
        List<ArticleTag> articleTags = articleTagMapper.selectList(wrapper);

        return articleTags.stream().map(at -> {
            TagVO tag = new TagVO();
            tag.setId(at.getTagId());
            // 这里简化处理，实际应该查询标签名
            return tag;
        }).collect(Collectors.toList());
    }

    /**
     * 保存文章标签关联
     */
    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(tagId);
            articleTagMapper.insert(articleTag);
        }
    }

    /**
     * 生成文章摘要
     */
    private String generateSummary(String content) {
        if (content == null) return "";
        // 去除Markdown标记并截取前200字符
        String text = content.replaceAll("#+ ", "")
                            .replaceAll("\\*+", "")
                            .replaceAll("`+", "")
                            .replaceAll("\\[.+?\\]\\(.+?\\)", "")
                            .trim();
        return text.length() > 200 ? text.substring(0, 200) + "..." : text;
    }
}

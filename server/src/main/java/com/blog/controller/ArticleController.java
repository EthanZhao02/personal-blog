package com.blog.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.ArticleDTO;
import com.blog.service.ArticleService;
import com.blog.utils.JwtUtil;
import com.blog.vo.ArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 文章控制器
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取文章列表
     */
    @GetMapping("/list")
    public Result<PageResult<ArticleVO>> getArticleList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword) {
        return articleService.getArticleList(current, size, categoryId, tagId, keyword);
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/detail/{id}")
    public Result<ArticleVO> getArticleDetail(@PathVariable Long id) {
        return articleService.getArticleDetail(id);
    }

    /**
     * 获取当前用户的文章列表（包含未发布的）
     */
    @GetMapping("/my")
    public Result<PageResult<ArticleVO>> getMyArticles(HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "50") Integer size) {
        Long userId = getUserIdFromRequest(request);
        return articleService.getMyArticles(userId, current, size);
    }

    /**
     * 发布文章
     */
    @PostMapping("/publish")
    public Result<ArticleVO> publishArticle(HttpServletRequest request, @RequestBody ArticleDTO articleDTO) {
        Long userId = getUserIdFromRequest(request);
        return articleService.publishArticle(userId, articleDTO);
    }

    /**
     * 更新文章
     */
    @PutMapping("/update")
    public Result<ArticleVO> updateArticle(HttpServletRequest request, @RequestBody ArticleDTO articleDTO) {
        Long userId = getUserIdFromRequest(request);
        return articleService.updateArticle(userId, articleDTO);
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteArticle(HttpServletRequest request, @PathVariable Long id) {
        Long userId = getUserIdFromRequest(request);
        return articleService.deleteArticle(userId, id);
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserId(token);
    }
}

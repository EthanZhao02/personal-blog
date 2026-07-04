package com.blog.config;

import com.blog.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "https://ethanzhao02.github.io",
                        "https://ethanzhao02.github.io/personal-blog",
                        "https://*.pages.dev",
                        "https://personal-blog-api-muty.onrender.com"
                )
                // 兼容老配置
                .allowedOrigins("https://ethanzhao02.github.io", "https://ethanzhao02.github.io/personal-blog", "https://personal-blog-api-muty.onrender.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 用户相关 - 公开
                        "/user/login",
                        "/user/register",
                        // 文章相关 - 公开
                        "/article/list",
                        "/article/detail/**",
                        // 分类和标签 - 公开
                        "/category/list",
                        "/tag/list",
                        // 评论相关 - 公开（支持游客评论）
                        "/comment/list/**",
                        "/comment/add",
                        // 留言相关 - 公开
                        "/message/list",
                        "/message/add",
                        // 友链相关 - 公开
                        "/friend/link/list",
                        "/friend/link/all",
                        "/friend/link/apply",
                        // 项目相关 - 公开
                        "/project/list",
                        "/project/all",
                        // 站点访问统计 - 公开
                        "/site/stats",
                        "/site/stats/**",
                        // 个人资料 - 公开读取，写由 Controller 层鉴权
                        "/profile",
                        "/profile/**",
                        // 上传文件 - 公开
                        "/upload/**",
                        "/uploads/**",
                        // 错误页面
                        "/error"
                );
    }

    /**
     * 静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = System.getenv("FILE_UPLOAD_PATH");
        if (uploadPath == null || uploadPath.isEmpty()) {
            uploadPath = "uploads/";
        }
        if (!uploadPath.endsWith("/") && !uploadPath.endsWith("/")) {
            uploadPath = uploadPath + "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}

package com.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 数据库初始化器 - 自动修复字段缺失、初始化默认数据等问题
 */
@Slf4j
@Component
@Order(1)
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${admin.default-password:}")
    private String adminPassword;

    // TiDB Cloud 中所有表都带 test. 前缀
    private static final String USER_TABLE = "test.blog_user";
    private static final String MESSAGE_TABLE = "test.blog_message";
    private static final String COMMENT_TABLE = "test.blog_comment";
    private static final String ARTICLE_TABLE = "test.blog_article";
    private static final String CATEGORY_TABLE = "test.blog_category";
    private static final String TAG_TABLE = "test.blog_tag";
    private static final String FRIENDLINK_TABLE = "test.blog_friend_link";
    private static final String PROJECT_TABLE = "test.blog_project";
    private static final String PROFILE_TABLE = "test.blog_profile";

    @Override
    public void run(String... args) throws Exception {
        log.info("=== 开始数据库自检与修复 ===");
        ensureUserIsAdminColumn();
        ensureAdminUserExists();
        ensureUserIsAdminValue();
        ensureMessageColumns();
        ensureCommentColumns();
        ensureArticleColumns();
        ensureFriendLinkColumns();
        ensureProjectColumns();
        ensureCategoryColumns();
        ensureCategoryData();
        ensureTagData();
        ensureProfileTable();
        ensureDefaultProfile();
        log.info("=== 数据库自检完成 ===");
    }

    /**
     * 确保 user 表有 is_admin 字段
     */
    private void ensureUserIsAdminColumn() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = 'test' " +
                "AND TABLE_NAME = 'blog_user' " +
                "AND COLUMN_NAME = 'is_admin'",
                Integer.class
            );

            if (count == null || count == 0) {
                log.warn("{} 表缺少 is_admin 字段，正在自动添加...", USER_TABLE);
                jdbcTemplate.execute("ALTER TABLE " + USER_TABLE + " ADD COLUMN is_admin TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否管理员 1是 0否'");
                log.info("✓ 成功添加 is_admin 字段");
            } else {
                log.info("✓ is_admin 字段已存在");
            }
        } catch (Exception e) {
            log.error("✗ 添加 is_admin 字段失败", e);
        }
    }

    /**
     * 确保 admin 用户存在，不存在则自动创建（默认密码 123456）
     */
    private void ensureAdminUserExists() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + USER_TABLE + " WHERE username = 'admin'",
                Integer.class
            );
            if (count == null || count == 0) {
                log.warn("未找到 admin 用户，正在自动创建默认管理员...");
                // 密码从环境变量 ADMIN_DEFAULT_PASSWORD 读取，不设则跳过创建
                String pwd = adminPassword;
                if (pwd == null || pwd.isBlank()) {
                    pwd = System.getenv("ADMIN_DEFAULT_PASSWORD");
                }
                if (pwd == null || pwd.isBlank()) {
                    log.warn("未设置 ADMIN_DEFAULT_PASSWORD 环境变量，跳过自动创建 admin 用户");
                    return;
                }
                String hash = DigestUtils.md5DigestAsHex(pwd.getBytes(StandardCharsets.UTF_8));
                jdbcTemplate.update(
                    "INSERT INTO " + USER_TABLE + " (username, password, nickname, email, is_admin, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, NOW(), NOW())",
                    "admin", hash, "Admin", "admin@blog.local", 1
                );
                log.info("✓ 已创建默认管理员账号");
            } else {
                log.info("✓ admin 用户已存在");
            }
        } catch (Exception e) {
            log.error("✗ 创建 admin 用户失败", e);
        }
    }

    /**
     * 确保 admin 用户被标记为管理员
     */
    private void ensureUserIsAdminValue() {
        try {
            int updated = jdbcTemplate.update(
                "UPDATE " + USER_TABLE + " SET is_admin = 1 WHERE username = 'admin' AND (is_admin IS NULL OR is_admin = 0)"
            );
            if (updated > 0) {
                log.info("✓ 已将 admin 用户设为管理员 (更新 {} 条)", updated);
            }
        } catch (Exception e) {
            log.error("✗ 设置 admin 权限失败", e);
        }
    }

    /**
     * 确保 message 表包含实体类所有字段
     */
    private void ensureMessageColumns() {
        ensureColumns(MESSAGE_TABLE, List.of(
                Map.of("name", "nickname", "type", "VARCHAR(100)"),
                Map.of("name", "email", "type", "VARCHAR(100)"),
                Map.of("name", "website", "type", "VARCHAR(200)"),
                Map.of("name", "avatar", "type", "VARCHAR(500)"),
                Map.of("name", "reply", "type", "VARCHAR(500)"),
                Map.of("name", "parent_id", "type", "BIGINT"),
                Map.of("name", "is_show", "type", "TINYINT(1)"),
                Map.of("name", "ip_address", "type", "VARCHAR(100)")
        ));
    }

    /**
     * 确保 comment 表包含实体类所有字段
     */
    private void ensureCommentColumns() {
        ensureColumns(COMMENT_TABLE, List.of(
                Map.of("name", "nickname", "type", "VARCHAR(100)"),
                Map.of("name", "email", "type", "VARCHAR(100)"),
                Map.of("name", "website", "type", "VARCHAR(200)"),
                Map.of("name", "content", "type", "TEXT"),
                Map.of("name", "article_id", "type", "BIGINT"),
                Map.of("name", "user_id", "type", "BIGINT"),
                Map.of("name", "parent_id", "type", "BIGINT"),
                Map.of("name", "create_time", "type", "DATETIME")
        ));
    }

    /**
     * 确保 article 表包含必要字段
     */
    private void ensureArticleColumns() {
        ensureColumns(ARTICLE_TABLE, List.of(
                Map.of("name", "attachments", "type", "TEXT"),
                Map.of("name", "is_published", "type", "TINYINT(1)"),
                Map.of("name", "cover_image", "type", "VARCHAR(500)"),
                Map.of("name", "summary", "type", "VARCHAR(500)")
        ));
    }

    /**
     * 确保 friend_link 表包含实体类所有字段
     */
    private void ensureFriendLinkColumns() {
        ensureColumns(FRIENDLINK_TABLE, List.of(
                Map.of("name", "category", "type", "VARCHAR(50)"),
                Map.of("name", "email", "type", "VARCHAR(100)"),
                Map.of("name", "is_active", "type", "TINYINT(1)"),
                Map.of("name", "sort_order", "type", "INT"),
                Map.of("name", "update_time", "type", "DATETIME")
        ));
    }

    /**
     * 确保 project 表包含实体类所有字段
     */
    private void ensureProjectColumns() {
        ensureColumns(PROJECT_TABLE, List.of(
                Map.of("name", "name", "type", "VARCHAR(200)"),
                Map.of("name", "description", "type", "TEXT"),
                Map.of("name", "url", "type", "VARCHAR(500)"),
                Map.of("name", "github_url", "type", "VARCHAR(500)"),
                Map.of("name", "icon", "type", "VARCHAR(50)"),
                Map.of("name", "image_url", "type", "VARCHAR(500)"),
                Map.of("name", "tag", "type", "VARCHAR(50)"),
                Map.of("name", "tech_stack", "type", "TEXT"),
                Map.of("name", "badge", "type", "VARCHAR(50)"),
                Map.of("name", "category", "type", "VARCHAR(50)"),
                Map.of("name", "stats", "type", "TEXT"),
                Map.of("name", "status", "type", "VARCHAR(50)"),
                Map.of("name", "is_open_source", "type", "TINYINT(1)"),
                Map.of("name", "article_url", "type", "VARCHAR(500)"),
                Map.of("name", "sort_order", "type", "INT"),
                Map.of("name", "is_active", "type", "TINYINT(1)"),
                Map.of("name", "create_time", "type", "DATETIME"),
                Map.of("name", "update_time", "type", "DATETIME")
        ));
    }

    /**
     * 确保 category 表包含实体类所有字段
     */
    private void ensureCategoryColumns() {
        ensureColumns(CATEGORY_TABLE, List.of(
                Map.of("name", "sort", "type", "INT")
        ));
    }

    /**
     * 确保分类表有默认数据
     */
    private void ensureCategoryData() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + CATEGORY_TABLE, Integer.class
            );
            if (count == null || count == 0) {
                log.warn("分类表为空，正在初始化默认分类...");
                jdbcTemplate.update("INSERT INTO " + CATEGORY_TABLE + " (name, sort, create_time) VALUES ('技术', 1, NOW())");
                jdbcTemplate.update("INSERT INTO " + CATEGORY_TABLE + " (name, sort, create_time) VALUES ('生活', 2, NOW())");
                jdbcTemplate.update("INSERT INTO " + CATEGORY_TABLE + " (name, sort, create_time) VALUES ('随笔', 3, NOW())");
                jdbcTemplate.update("INSERT INTO " + CATEGORY_TABLE + " (name, sort, create_time) VALUES ('项目', 4, NOW())");
                log.info("✓ 已初始化默认分类");
            } else {
                log.info("✓ 分类数据已存在");
            }
        } catch (Exception e) {
            log.error("✗ 初始化分类数据失败", e);
        }
    }

    /**
     * 确保标签表有默认数据
     */
    private void ensureTagData() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + TAG_TABLE, Integer.class
            );
            if (count == null || count == 0) {
                log.warn("标签表为空，正在初始化默认标签...");
                String[] tags = {"Java", "Vue", "MySQL", "SpringBoot", "Spring", "JavaScript", "TypeScript", "Python", "Docker", "Git"};
                for (String tag : tags) {
                    jdbcTemplate.update("INSERT INTO " + TAG_TABLE + " (name, create_time) VALUES (?, NOW())", tag);
                }
                log.info("✓ 已初始化默认标签");
            } else {
                log.info("✓ 标签数据已存在");
            }
        } catch (Exception e) {
            log.error("✗ 初始化标签数据失败", e);
        }
    }

    /**
     * 通用字段补齐方法
     */
    private void ensureColumns(String tableName, List<Map<String, String>> columns) {
        String dbName = "test";
        String rawTable = tableName.replace("test.", "");
        for (Map<String, String> col : columns) {
            String colName = col.get("name");
            String colType = col.get("type");
            try {
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                    Integer.class, dbName, rawTable, colName
                );
                if (count == null || count == 0) {
                    log.warn("{} 表缺少 {} 字段，正在自动添加...", tableName, colName);
                    jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + colName + " " + colType);
                    log.info("✓ 成功添加 {} 字段", colName);
                }
            } catch (Exception e) {
                log.error("✗ 检查/添加 {}.{} 字段失败", tableName, colName, e);
            }
        }
    }

    private void ensureProfileTable() {
        ensureColumns(PROFILE_TABLE, java.util.List.of(
                java.util.Map.of("name", "name", "type", "VARCHAR(100)"),
                java.util.Map.of("name", "tagline", "type", "VARCHAR(200)"),
                java.util.Map.of("name", "bio", "type", "TEXT"),
                java.util.Map.of("name", "location", "type", "VARCHAR(100)"),
                java.util.Map.of("name", "status", "type", "VARCHAR(50)"),
                java.util.Map.of("name", "avatar", "type", "VARCHAR(500)"),
                java.util.Map.of("name", "skills", "type", "TEXT"),
                java.util.Map.of("name", "interests", "type", "TEXT"),
                java.util.Map.of("name", "socials", "type", "TEXT")
        ));
    }

    private void ensureDefaultProfile() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + PROFILE_TABLE, Integer.class
            );
            if (count == null || count == 0) {
                log.warn("个人资料表为空，正在初始化默认资料...");
                String skillsJson = "[{\"name\":\"Vue.js\",\"level\":85},{\"name\":\"Java/Spring\",\"level\":80},{\"name\":\"AI/LLM\",\"level\":75},{\"name\":\"Python\",\"level\":70}]";
                String interestsJson = "[\"AI/LLM\",\"Web Dev\",\"Open Source\",\"Knowledge Management\"]";
                String socialsJson = "[{\"name\":\"GitHub\",\"icon\":\"github\",\"url\":\"https://github.com/EthanZhao02\",\"color\":\"#333\",\"handle\":\"@EthanZhao02\"}]";
                jdbcTemplate.update("INSERT INTO " + PROFILE_TABLE + " (id, name, tagline, bio, location, status, skills, interests, socials) VALUES (1, 'E森赵', 'AI & Web Developer', '正在探索AI与Web开发的交汇点，专注知识管理和学习系统。', 'Earth', 'Available', ?, ?, ?)", skillsJson, interestsJson, socialsJson);
                log.info("✓ 已初始化默认个人资料");
            }
        } catch (Exception e) {
            log.error("✗ 初始化个人资料失败", e);
        }
    }
}

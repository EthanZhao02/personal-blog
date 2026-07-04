package com.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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

    // TiDB Cloud 中所有表都带 test. 前缀
    private static final String USER_TABLE = "test.blog_user";
    private static final String MESSAGE_TABLE = "test.blog_message";
    private static final String COMMENT_TABLE = "test.blog_comment";

    @Override
    public void run(String... args) throws Exception {
        log.info("=== 开始数据库自检与修复 ===");
        ensureUserIsAdminColumn();
        ensureAdminUserExists();
        ensureUserIsAdminValue();
        ensureMessageColumns();
        ensureCommentColumns();
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
                // MD5("123456") = e10adc3949ba59abbe56e057f20f883e
                jdbcTemplate.update(
                    "INSERT INTO " + USER_TABLE + " (username, password, nickname, email, is_admin, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, NOW(), NOW())",
                    "admin", "e10adc3949ba59abbe56e057f20f883e", "Admin", "admin@blog.local", 1
                );
                log.info("✓ 已创建默认管理员账号 admin / 123456");
            } else {
                log.info("✓ admin 用户已存在");
            }
        } catch (Exception e) {
            log.error("✗ 创建 admin 用户失败（可能表不存在，请先执行 init.sql）", e);
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
}

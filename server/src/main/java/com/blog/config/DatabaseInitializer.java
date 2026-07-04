package com.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库初始化器 - 自动修复字段缺失等问题
 */
@Slf4j
@Component
@Order(1)
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== 开始数据库自检与修复 ===");
        ensureUserIsAdminColumn();
        ensureUserIsAdminValue();
        log.info("=== 数据库自检完成 ===");
    }

    /**
     * 确保 user 表有 is_admin 字段
     */
    private void ensureUserIsAdminColumn() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "AND TABLE_NAME = 'blog_user' " +
                "AND COLUMN_NAME = 'is_admin'",
                Integer.class
            );

            if (count == null || count == 0) {
                log.warn("blog_user 表缺少 is_admin 字段，正在自动添加...");
                jdbcTemplate.execute("ALTER TABLE blog_user ADD COLUMN is_admin TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否管理员 1是 0否'");
                log.info("✓ 成功添加 is_admin 字段");
            } else {
                log.info("✓ is_admin 字段已存在");
            }
        } catch (Exception e) {
            log.error("✗ 添加 is_admin 字段失败", e);
        }
    }

    /**
     * 确保 admin 用户被标记为管理员
     */
    private void ensureUserIsAdminValue() {
        try {
            int updated = jdbcTemplate.update(
                "UPDATE blog_user SET is_admin = 1 WHERE username = 'admin' AND (is_admin IS NULL OR is_admin = 0)"
            );
            if (updated > 0) {
                log.info("✓ 已将 admin 用户设为管理员 (更新 {} 条)", updated);
            }
        } catch (Exception e) {
            log.error("✗ 设置 admin 权限失败", e);
        }
    }
}

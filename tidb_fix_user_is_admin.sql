-- 修复 user 表缺少 is_admin 列的问题
USE test;

-- 1. 添加 is_admin 列（如果不存在）
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = 'test' 
                   AND TABLE_NAME = 'blog_user' 
                   AND COLUMN_NAME = 'is_admin');

SET @sql = IF(@col_exists = 0, 
              'ALTER TABLE blog_user ADD COLUMN is_admin TINYINT(1) NOT NULL DEFAULT 0 COMMENT ''是否管理员 1是 0否''', 
              'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 将 admin 用户设为管理员
UPDATE blog_user SET is_admin = 1 WHERE username = 'admin';

-- 3. 验证表结构
DESCRIBE blog_user;

-- 4. 验证 admin 用户
SELECT id, username, nickname, is_admin FROM blog_user WHERE username = 'admin';

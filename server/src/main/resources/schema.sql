-- 自动补齐缺失字段（忽略已存在的字段）
ALTER TABLE test.blog_category ADD COLUMN sort INT;
ALTER TABLE test.blog_friend_link ADD COLUMN is_active TINYINT(1) DEFAULT 1;
ALTER TABLE test.blog_friend_link ADD COLUMN sort_order INT DEFAULT 0;
ALTER TABLE test.blog_friend_link ADD COLUMN update_time DATETIME;
ALTER TABLE test.blog_project ADD COLUMN update_time DATETIME;
ALTER TABLE test.blog_project ADD COLUMN sort_order INT DEFAULT 0;
ALTER TABLE test.blog_project ADD COLUMN is_active TINYINT(1) DEFAULT 1;

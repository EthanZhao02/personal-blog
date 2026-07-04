ALTER TABLE test.blog_category ADD COLUMN sort INT DEFAULT 0;
ALTER TABLE test.blog_project ADD COLUMN update_time DATETIME;
ALTER TABLE test.blog_friend_link ADD COLUMN is_active TINYINT(1) DEFAULT 1;

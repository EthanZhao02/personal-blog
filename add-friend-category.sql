-- 添加友链分类字段
ALTER TABLE friend_link ADD COLUMN category VARCHAR(50) DEFAULT 'tech' COMMENT '分类: tech/life/other';
UPDATE friend_link SET category = 'tech' WHERE category IS NULL;
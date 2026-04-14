-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog;

-- 用户表
DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  nickname VARCHAR(50) COMMENT '昵称',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(255) COMMENT '头像',
  create_time DATETIME COMMENT '创建时间',
  update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
DROP TABLE IF EXISTS category;
CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort INT DEFAULT 0 COMMENT '排序',
  create_time DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 标签表
DROP TABLE IF EXISTS tag;
CREATE TABLE tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
  name VARCHAR(50) NOT NULL COMMENT '标签名称',
  create_time DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 文章表
DROP TABLE IF EXISTS article;
CREATE TABLE article (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章ID',
  title VARCHAR(200) NOT NULL COMMENT '标题',
  content TEXT COMMENT '内容',
  summary VARCHAR(500) COMMENT '摘要',
  cover_image VARCHAR(255) COMMENT '封面图',
  category_id BIGINT COMMENT '分类ID',
  user_id BIGINT COMMENT '用户ID',
  view_count INT DEFAULT 0 COMMENT '浏览量',
  is_published TINYINT DEFAULT 1 COMMENT '是否发布',
  create_time DATETIME COMMENT '创建时间',
  update_time DATETIME COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 文章标签关联表
DROP TABLE IF EXISTS article_tag;
CREATE TABLE article_tag (
  article_id BIGINT COMMENT '文章ID',
  tag_id BIGINT COMMENT '标签ID',
  PRIMARY KEY (article_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- 评论表
DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  content VARCHAR(500) NOT NULL COMMENT '评论内容',
  article_id BIGINT COMMENT '文章ID',
  user_id BIGINT COMMENT '用户ID',
  parent_id BIGINT DEFAULT 0 COMMENT '父评论ID',
  create_time DATETIME COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 插入默认分类
INSERT INTO category (name, sort, create_time) VALUES 
('技术', 1, NOW()),
('生活', 2, NOW()),
('随笔', 3, NOW());

-- 插入默认标签
INSERT INTO tag (name, create_time) VALUES 
('Java', NOW()),
('Vue', NOW()),
('MySQL', NOW()),
('SpringBoot', NOW());

-- 插入测试用户（密码是 123456 的 MD5 值）
INSERT INTO user (username, password, nickname, email, create_time) VALUES 
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin@example.com', NOW());

-- 插入测试文章
INSERT INTO article (title, content, summary, category_id, user_id, view_count, create_time) VALUES 
('欢迎使用个人博客系统', 
'# 欢迎\n\n这是一个基于 Vue3 + SpringBoot + MySQL 构建的个人博客系统。\n\n## 功能特性\n\n- 用户注册登录\n- 文章发布管理\n- 分类标签管理\n- 评论互动\n\n## 技术栈\n\n### 前端\n- Vue3\n- Vite\n- Element Plus\n- Pinia\n\n### 后端\n- SpringBoot\n- MyBatis-Plus\n- MySQL\n- JWT\n\n开始你的博客之旅吧！',
'欢迎使用个人博客系统，基于 Vue3 + SpringBoot + MySQL 构建', 
1, 1, 100, NOW());

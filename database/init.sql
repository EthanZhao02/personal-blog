-- ============================================
-- 个人博客系统 - 数据库初始化脚本
-- 适用于 MySQL 8.0+
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
  nickname VARCHAR(50) COMMENT '昵称',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(255) COMMENT '头像URL',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 分类表
-- ============================================
DROP TABLE IF EXISTS category;
CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort INT DEFAULT 0 COMMENT '排序（越小越靠前）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- ============================================
-- 3. 标签表
-- ============================================
DROP TABLE IF EXISTS tag;
CREATE TABLE tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
  name VARCHAR(50) NOT NULL COMMENT '标签名称',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ============================================
-- 4. 文章表
-- ============================================
DROP TABLE IF EXISTS article;
CREATE TABLE article (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章ID',
  title VARCHAR(200) NOT NULL COMMENT '标题',
  content TEXT COMMENT '内容（Markdown）',
  summary VARCHAR(500) COMMENT '摘要',
  cover_image VARCHAR(255) COMMENT '封面图URL',
  category_id BIGINT COMMENT '分类ID',
  user_id BIGINT COMMENT '作者ID',
  view_count INT DEFAULT 0 COMMENT '浏览量',
  is_published TINYINT DEFAULT 1 COMMENT '是否发布：0-草稿 1-已发布',
  attachments TEXT COMMENT '附件JSON',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL,
  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- ============================================
-- 5. 文章标签关联表
-- ============================================
DROP TABLE IF EXISTS article_tag;
CREATE TABLE article_tag (
  article_id BIGINT COMMENT '文章ID',
  tag_id BIGINT COMMENT '标签ID',
  PRIMARY KEY (article_id, tag_id),
  FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
  FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ============================================
-- 6. 评论表
-- ============================================
DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  content VARCHAR(500) NOT NULL COMMENT '评论内容',
  article_id BIGINT COMMENT '文章ID',
  user_id BIGINT COMMENT '评论用户ID',
  parent_id BIGINT DEFAULT 0 COMMENT '父评论ID（0为顶级评论）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ============================================
-- 7. 留言板表
-- ============================================
DROP TABLE IF EXISTS message;
CREATE TABLE message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '留言ID',
  nickname VARCHAR(50) NOT NULL COMMENT '留言者昵称',
  email VARCHAR(100) DEFAULT '' COMMENT '邮箱',
  website VARCHAR(255) DEFAULT '' COMMENT '网站地址',
  content VARCHAR(500) NOT NULL COMMENT '留言内容',
  avatar VARCHAR(255) DEFAULT '' COMMENT '头像URL',
  ip_address VARCHAR(50) DEFAULT '' COMMENT 'IP地址',
  parent_id BIGINT DEFAULT 0 COMMENT '父留言ID（0为顶级留言）',
  reply VARCHAR(500) DEFAULT '' COMMENT '博主回复内容（已废弃，使用子留言替代）',
  is_show TINYINT DEFAULT 1 COMMENT '是否显示：0-隐藏 1-显示',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_create_time (create_time),
  KEY idx_is_show (is_show),
  KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言板表';

-- ============================================
-- 8. 友链表
-- ============================================
DROP TABLE IF EXISTS friend_link;
CREATE TABLE friend_link (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '友链ID',
  name VARCHAR(50) NOT NULL COMMENT '网站名称',
  description VARCHAR(200) DEFAULT '' COMMENT '网站描述',
  url VARCHAR(255) NOT NULL COMMENT '网站地址',
  avatar VARCHAR(255) DEFAULT '' COMMENT '头像/图标URL',
  category VARCHAR(50) DEFAULT '' COMMENT '分类',
  email VARCHAR(100) DEFAULT '' COMMENT '联系邮箱',
  is_active TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用 1-启用',
  sort_order INT DEFAULT 0 COMMENT '排序（越小越靠前）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_sort_order (sort_order),
  KEY idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='友情链接表';

-- ============================================
-- 9. 站点访问统计表
-- ============================================
DROP TABLE IF EXISTS site_visit;
CREATE TABLE site_visit (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '访问ID',
  visitor_id VARCHAR(80) NOT NULL COMMENT '访客标识',
  path VARCHAR(500) DEFAULT '' COMMENT '访问路径',
  user_agent VARCHAR(500) DEFAULT '' COMMENT '浏览器信息',
  ip_address VARCHAR(100) DEFAULT '' COMMENT '访问IP',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  KEY idx_visitor_id (visitor_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点访问统计表';

-- ============================================
-- 10. 项目展示表
-- ============================================
DROP TABLE IF EXISTS project;
CREATE TABLE project (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目ID',
  name VARCHAR(100) NOT NULL COMMENT '项目名称',
  description VARCHAR(500) DEFAULT '' COMMENT '项目描述',
  url VARCHAR(255) DEFAULT '' COMMENT '项目演示地址',
  github_url VARCHAR(255) DEFAULT '' COMMENT 'GitHub仓库地址',
  icon VARCHAR(255) DEFAULT '' COMMENT '项目图标URL',
  image_url VARCHAR(255) DEFAULT '' COMMENT '项目截图URL',
  tag VARCHAR(100) DEFAULT '' COMMENT '项目标签',
  tech_stack VARCHAR(200) DEFAULT '' COMMENT '技术栈',
  badge VARCHAR(255) DEFAULT '' COMMENT '徽章/状态信息',
  category VARCHAR(50) DEFAULT '' COMMENT '项目分类',
  stats VARCHAR(200) DEFAULT '' COMMENT '统计数据（如 star 数）',
  status VARCHAR(50) DEFAULT 'completed' COMMENT '项目状态：completed/in-progress/planning',
  is_open_source TINYINT DEFAULT 1 COMMENT '是否开源：0-否 1-是',
  article_url VARCHAR(255) DEFAULT '' COMMENT '关联文章地址',
  sort_order INT DEFAULT 0 COMMENT '排序',
  is_active TINYINT DEFAULT 1 COMMENT '是否显示：0-隐藏 1-显示',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_sort_order (sort_order),
  KEY idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目展示表';


-- ============================================
-- 初始数据
-- ============================================

-- 插入默认用户（用户名: admin  密码: 123456）
INSERT INTO user (username, password, nickname, email, create_time) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '博主', 'admin@example.com', NOW());

-- 插入默认分类
INSERT INTO category (name, sort, create_time) VALUES
('技术', 1, NOW()),
('生活', 2, NOW()),
('随笔', 3, NOW()),
('项目', 4, NOW());

-- 插入默认标签
INSERT INTO tag (name, create_time) VALUES
('Java', NOW()),
('Vue', NOW()),
('MySQL', NOW()),
('SpringBoot', NOW()),
('Spring', NOW()),
('JavaScript', NOW()),
('TypeScript', NOW()),
('Python', NOW()),
('Docker', NOW()),
('Git', NOW());

-- 插入示例文章
INSERT INTO article (title, content, summary, cover_image, category_id, user_id, view_count, is_published, create_time) VALUES
('欢迎使用个人博客系统',
'# 欢迎来到我的博客 🚀

这是一个基于 **Vue3 + SpringBoot + MySQL** 构建的个人博客系统。

## 功能特性

- 📝 文章发布与管理
- 🏷️ 分类和标签
- 💬 评论互动
- 📋 留言板
- 🔗 友链管理
- 💼 项目展示

## 技术栈

### 前端
- Vue 3 + Composition API
- Vite 构建工具
- Element Plus UI
- Pinia 状态管理
- Vue Router

### 后端
- Spring Boot
- MyBatis-Plus
- MySQL 数据库
- JWT 认证

> 开始你的博客之旅吧！

```java
public class Blog {
    public static void main(String[] args) {
        System.out.println("Hello, Blog!");
    }
}
```',
'欢迎使用个人博客系统，基于 Vue3 + SpringBoot + MySQL 构建，提供完整的博客功能',
'',
1, 1, 100, 1, NOW());

-- 插入示例友链
INSERT INTO friend_link (name, description, url, avatar, is_active, sort_order, create_time) VALUES
('idealclover', 'NJUPT→SE，Java后端', 'https://www.idealclover.top/', '', 1, 1, NOW()),
('souta', 'Souta的博客 | 简洁分享', 'https://blog.souta.cc/', '', 1, 2, NOW()),
('qwqwq', '一个前端博客', 'https://qwqwq.com/', '', 1, 3, NOW());

-- 插入示例留言
INSERT INTO message (nickname, email, content, avatar, is_show, create_time) VALUES
('小明', 'xiaoming@example.com', '博主博客写得太棒了，已 star ~ 🚀', '', 1, NOW()),
('路过的小站', 'visitor@example.com', '友链已添加，期待更多交流 ✨', '', 1, NOW()),
('技术爱好者', '', '这个 Vue3 + SpringBoot 的架构很清晰，学到了！', '', 1, NOW());

-- 插入示例项目
INSERT INTO project (name, description, url, github_url, tech_stack, category, status, is_open_source, sort_order, is_active, create_time) VALUES
('个人博客系统', '基于 Vue3 + SpringBoot 的个人博客，支持文章管理、评论互动、留言板等功能', 'https://blog.example.com', 'https://github.com/EthanZhao02/personal-blog', 'Vue3,SpringBoot,MySQL,Element Plus', 'Web开发', 'completed', 1, 1, 1, NOW()),
('在线简历生成器', '输入信息自动生成美观的在线简历页面', 'https://resume.example.com', 'https://github.com/example/resume-gen', 'React,TailwindCSS,Node.js', '工具类', 'completed', 1, 2, 1, NOW()),
('天气预报小程序', '支持多城市查询的未来7天天气预报应用', '', 'https://github.com/example/weather-app', '微信小程序,API', '小程序', 'completed', 1, 3, 1, NOW());

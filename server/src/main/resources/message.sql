-- 留言板表
CREATE TABLE IF NOT EXISTS `message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nickname` varchar(50) NOT NULL COMMENT '留言者昵称',
  `email` varchar(100) DEFAULT '' COMMENT '邮箱',
  `content` varchar(500) NOT NULL COMMENT '留言内容',
  `avatar` varchar(255) DEFAULT '' COMMENT '头像URL',
  `ip_address` varchar(50) DEFAULT '' COMMENT 'IP地址',
  `is_show` tinyint DEFAULT 1 COMMENT '是否显示 0-隐藏 1-显示',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_show` (`is_show`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言板';

-- 友链表
CREATE TABLE IF NOT EXISTS `friend_link` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '网站名称',
  `description` varchar(200) DEFAULT '' COMMENT '网站描述',
  `url` varchar(255) NOT NULL COMMENT '网站地址',
  `avatar` varchar(255) DEFAULT '' COMMENT '头像/图标',
  `email` varchar(100) DEFAULT '' COMMENT '联系邮箱',
  `is_active` tinyint DEFAULT 1 COMMENT '是否启用 0-禁用 1-启用',
  `sort_order` int DEFAULT 0 COMMENT '排序，越小越靠前',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='友情链接';

-- 插入一些示例友链
INSERT INTO `friend_link` (`name`, `description`, `url`, `avatar`, `is_active`, `sort_order`) VALUES
('idealclover', 'NJUPT→SE，Java后端 | 博客导航', 'https://www.idealclover.top/', '', 1, 1),
('souta', 'Souta的博客 | 简洁分享', 'https://blog.souta.cc/', '', 1, 2),
('qwqwq', '一个前端博客 | 分享技术', 'https://qwqwq.com/', '', 1, 3);

-- 插入示例留言
INSERT INTO `message` (`nickname`, `email`, `content`, `avatar`, `is_show`) VALUES
('小明', 'xiaoming@example.com', '博主博客写得太棒了，已 star ~ 🚀', '', 1),
('路过的小站', 'visitor@example.com', '友链已添加，期待更多交流 ✨', '', 1),
('技术爱好者', '', '这个 Vue3 + SpringBoot 的架构很清晰，学到了！', '', 1);

-- 项目表
CREATE TABLE IF NOT EXISTS `project` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `description` TEXT COMMENT '项目描述',
  `url` VARCHAR(500) COMMENT '项目链接',
  `icon` VARCHAR(50) DEFAULT '🛠️' COMMENT '图标emoji',
  `tag` VARCHAR(50) COMMENT '标签（如 Java）',
  `badge` VARCHAR(50) COMMENT '角标（如 NEW）',
  `stats` VARCHAR(100) COMMENT '统计数据（如 ⭐ 1.2k）',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `is_active` INT DEFAULT 1 COMMENT '1=上线 0=下线',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_active_order` (`is_active`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 插入示例数据
INSERT INTO `project` (`name`, `description`, `url`, `icon`, `tag`, `badge`, `stats`, `sort_order`, `is_active`) VALUES
('AI 天气助手', '基于大模型的智能天气预报工具，支持多语言和极简 UI', 'https://example.com', '🌤️', 'Python', 'NEW', '🚀 1.5k', 1, 1),
('博客系统', '前后端分离的个人博客，支持 Markdown 编辑与友链管理', 'https://example.com', '📝', 'Java', NULL, '⭐ 800', 2, 1),
('开源图床', '支持 CDN 加速的在线图片托管服务', 'https://example.com', '🖼️', 'Node.js', NULL, '👀 3.2k', 3, 1);

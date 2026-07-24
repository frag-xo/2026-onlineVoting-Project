-- 投票系统扩展表结构

-- 1. 投票收藏表
CREATE TABLE IF NOT EXISTS `vote_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `vote_id` BIGINT NOT NULL COMMENT '投票ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vote_user` (`vote_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票收藏表';

-- 2. 投票评论表
CREATE TABLE IF NOT EXISTS `vote_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `vote_id` BIGINT NOT NULL COMMENT '投票ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_vote_id` (`vote_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票评论表';

-- 3. 通知表
CREATE TABLE IF NOT EXISTS `vote_notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
  `type` VARCHAR(20) NOT NULL COMMENT '通知类型',
  `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '通知内容',
  `vote_id` BIGINT DEFAULT NULL COMMENT '关联投票ID',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 4. 修改投票表，添加审核状态字段
ALTER TABLE `vote` ADD COLUMN `audit_status` TINYINT DEFAULT 1 COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝';
ALTER TABLE `vote` ADD COLUMN `audit_msg` VARCHAR(200) DEFAULT NULL COMMENT '审核备注';
ALTER TABLE `vote` ADD COLUMN `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名投票：0-否，1-是';
ALTER TABLE `vote` ADD COLUMN `vote_type` TINYINT DEFAULT 1 COMMENT '投票类型：1-单选，2-多选';

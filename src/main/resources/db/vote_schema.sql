-- 在线投票系统建表 SQL
-- 数据库: ai_health

-- 1. 账号表
CREATE TABLE IF NOT EXISTS `account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '账号ID',
  `uname` VARCHAR(50) NOT NULL COMMENT '用户名',
  `pwd` VARCHAR(100) NOT NULL COMMENT '密码',
  `phone_number` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `utype` VARCHAR(20) DEFAULT 'ROLE_3' COMMENT '角色: ROLE_1管理员, ROLE_3普通用户',
  `realname` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删, 1-已删',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uname` (`uname`) COMMENT '用户名唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号表';

-- 2. 投票表

CREATE TABLE IF NOT EXISTS `vote` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '投票ID',
  `title` VARCHAR(200) NOT NULL COMMENT '投票标题',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '投票描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-未开始, 1-进行中, 2-已结束',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `creator_id` BIGINT DEFAULT NULL COMMENT '创建者ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删, 1-已删',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票表';

-- 3. 投票选项表
CREATE TABLE IF NOT EXISTS `vote_option` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '选项ID',
  `vote_id` BIGINT NOT NULL COMMENT '所属投票ID',
  `option_text` VARCHAR(200) NOT NULL COMMENT '选项内容',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删, 1-已删',
  PRIMARY KEY (`id`),
  KEY `idx_vote_id` (`vote_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票选项表';

-- 4. 投票记录表（防重复投票）
CREATE TABLE IF NOT EXISTS `vote_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `vote_id` BIGINT NOT NULL COMMENT '投票ID',
  `option_id` BIGINT NOT NULL COMMENT '选项ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '投票时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删, 1-已删',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vote_user` (`vote_id`, `user_id`) COMMENT '同一投票只能投一次',
  KEY `idx_vote_id` (`vote_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票记录表';

-- 投票系统完整表结构

-- 1. 用户积分表
CREATE TABLE IF NOT EXISTS `user_points` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '积分ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `points` INT DEFAULT 0 COMMENT '积分余额',
  `total_earned` INT DEFAULT 0 COMMENT '累计获得积分',
  `total_spent` INT DEFAULT 0 COMMENT '累计消费积分',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分表';

-- 2. 积分记录表
CREATE TABLE IF NOT EXISTS `points_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `points` INT NOT NULL COMMENT '积分变动',
  `type` VARCHAR(20) NOT NULL COMMENT '类型',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 3. 转盘奖品表
CREATE TABLE IF NOT EXISTS `wheel_prize` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '奖品ID',
  `name` VARCHAR(100) NOT NULL COMMENT '奖品名称',
  `points` INT DEFAULT 0 COMMENT '奖品积分',
  `probability` DECIMAL(5,2) DEFAULT 0 COMMENT '中奖概率',
  `color` VARCHAR(20) DEFAULT '#4361ee' COMMENT '颜色',
  `icon` VARCHAR(50) DEFAULT '🎁' COMMENT '图标',
  `is_active` TINYINT DEFAULT 1 COMMENT '是否启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转盘奖品表';

-- 4. 转盘记录表
CREATE TABLE IF NOT EXISTS `wheel_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `vote_id` BIGINT NOT NULL COMMENT '投票ID',
  `prize_id` BIGINT NOT NULL COMMENT '奖品ID',
  `points` INT DEFAULT 0 COMMENT '获得积分',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转盘记录表';

-- 5. 投票分组表
CREATE TABLE IF NOT EXISTS `vote_group` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分组ID',
  `name` VARCHAR(100) NOT NULL COMMENT '分组名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '分组描述',
  `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票分组表';

-- 6. 审核记录表
CREATE TABLE IF NOT EXISTS `vote_audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '审核ID',
  `vote_id` BIGINT NOT NULL COMMENT '投票ID',
  `auditor_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
  `status` TINYINT DEFAULT 0 COMMENT '审核状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vote_id` (`vote_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

-- 7. 修改投票表，添加定时发布字段
ALTER TABLE `vote` ADD COLUMN `publish_time` DATETIME DEFAULT NULL COMMENT '定时发布时间';
ALTER TABLE `vote` ADD COLUMN `group_id` BIGINT DEFAULT NULL COMMENT '所属分组ID';

-- 8. 插入默认转盘奖品
INSERT INTO `wheel_prize` (`name`, `points`, `probability`, `color`, `icon`) VALUES
('10积分', 10, 30.00, '#4361ee', '🎯'),
('20积分', 20, 25.00, '#f72585', '🌟'),
('50积分', 50, 15.00, '#06d6a0', '💎'),
('100积分', 100, 10.00, '#ffd166', '🏆'),
('谢谢参与', 0, 20.00, '#adb5bd', '😊');

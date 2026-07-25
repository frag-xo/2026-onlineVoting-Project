-- ===================================================
-- 社区功能：好友关系表
-- ===================================================
CREATE TABLE IF NOT EXISTS `user_friend` (
    `id`          BIGINT    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT    NOT NULL COMMENT '发起方用户ID',
    `friend_id`   BIGINT    NOT NULL COMMENT '接收方用户ID',
    `status`      TINYINT   NOT NULL DEFAULT 0 COMMENT '状态：0-待确认，1-已同意，2-已拒绝',
    `create_time` DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time` DATETIME  NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_friend_id` (`friend_id`),
    UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友关系表';

-- ===================================================
-- 社区功能：聊天消息表
-- ===================================================
CREATE TABLE IF NOT EXISTS `chat_message` (
    `id`            BIGINT    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `from_user_id`  BIGINT    NOT NULL COMMENT '发送方用户ID',
    `to_user_id`    BIGINT    NOT NULL COMMENT '接收方用户ID',
    `content`       TEXT      NOT NULL COMMENT '消息内容',
    `is_read`       TINYINT   NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `create_time`   DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    INDEX `idx_from_user` (`from_user_id`),
    INDEX `idx_to_user` (`to_user_id`),
    INDEX `idx_conversation` (`from_user_id`, `to_user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

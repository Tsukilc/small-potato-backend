-- 点赞记录表
CREATE TABLE IF NOT EXISTS `like_record` (
    `id` bigint(20) NOT NULL COMMENT '主键ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `target_id` bigint(20) NOT NULL COMMENT '目标ID',
    `type` int(11) NOT NULL COMMENT '点赞类型：1-笔记 2-评论 3-回复',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '点赞状态：1-点赞 0-取消',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target_type` (`user_id`, `target_id`, `type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_target_id_type` (`target_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

-- 点赞计数表
CREATE TABLE IF NOT EXISTS `like_count` (
    `id` bigint(20) NOT NULL COMMENT '主键ID',
    `target_id` bigint(20) NOT NULL COMMENT '目标ID',
    `type` int(11) NOT NULL COMMENT '点赞类型：1-笔记 2-评论 3-回复',
    `count` bigint(20) NOT NULL DEFAULT '0' COMMENT '点赞数量',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_target_id_type` (`target_id`, `type`),
    KEY `idx_target_id` (`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞计数表'; 
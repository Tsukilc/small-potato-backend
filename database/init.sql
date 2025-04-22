-- 创建数据库
CREATE DATABASE IF NOT EXISTS small_potato_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS small_potato_note DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS small_potato_interaction DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS small_potato_file DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS small_potato_search DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS small_potato_message DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS small_potato_recommend DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS small_potato_security DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS small_potato_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户服务相关表
USE small_potato_user;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id VARCHAR(32) PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    bio VARCHAR(200) DEFAULT NULL COMMENT '个人简介',
    gender VARCHAR(10) DEFAULT '保密' COMMENT '性别',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户关注表
CREATE TABLE IF NOT EXISTS t_user_follow (
    id VARCHAR(32) PRIMARY KEY COMMENT '关注ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    follow_user_id VARCHAR(32) NOT NULL COMMENT '被关注用户ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_user_follow (user_id, follow_user_id),
    KEY idx_user_id (user_id),
    KEY idx_follow_user_id (follow_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- 用户屏蔽表
CREATE TABLE IF NOT EXISTS t_user_block (
    id VARCHAR(32) PRIMARY KEY COMMENT '屏蔽ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    block_user_id VARCHAR(32) NOT NULL COMMENT '被屏蔽用户ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_user_block (user_id, block_user_id),
    KEY idx_user_id (user_id),
    KEY idx_block_user_id (block_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户屏蔽表';

-- 笔记服务相关表
USE small_potato_note;

-- 笔记表
CREATE TABLE IF NOT EXISTS t_note (
    id VARCHAR(32) PRIMARY KEY COMMENT '笔记ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    user_id VARCHAR(32) NOT NULL COMMENT '作者ID',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    collect_count INT DEFAULT 0 COMMENT '收藏数',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记表';

-- 笔记图片表
CREATE TABLE IF NOT EXISTS t_note_image (
    id VARCHAR(32) PRIMARY KEY COMMENT '图片ID',
    note_id VARCHAR(32) NOT NULL COMMENT '笔记ID',
    url VARCHAR(255) NOT NULL COMMENT '图片URL',
    order_num INT NOT NULL COMMENT '排序号',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    KEY idx_note_id (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记图片表';

-- 标签表
CREATE TABLE IF NOT EXISTS t_tag (
    id VARCHAR(32) PRIMARY KEY COMMENT '标签ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    use_count INT DEFAULT 0 COMMENT '使用次数',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 笔记标签关联表
CREATE TABLE IF NOT EXISTS t_note_tag (
    id VARCHAR(32) PRIMARY KEY COMMENT '关联ID',
    note_id VARCHAR(32) NOT NULL COMMENT '笔记ID',
    tag_id VARCHAR(32) NOT NULL COMMENT '标签ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_note_tag (note_id, tag_id),
    KEY idx_note_id (note_id),
    KEY idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记标签关联表';

-- 交互服务相关表
USE small_potato_interaction;

-- 评论表
CREATE TABLE IF NOT EXISTS t_comment (
    id VARCHAR(32) PRIMARY KEY COMMENT '评论ID',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    user_id VARCHAR(32) NOT NULL COMMENT '评论者ID',
    note_id VARCHAR(32) NOT NULL COMMENT '笔记ID',
    parent_id VARCHAR(32) DEFAULT NULL COMMENT '父评论ID，为NULL表示一级评论',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    KEY idx_note_id (note_id),
    KEY idx_user_id (user_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 点赞表
CREATE TABLE IF NOT EXISTS t_like (
    id VARCHAR(32) PRIMARY KEY COMMENT '点赞ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    target_id VARCHAR(32) NOT NULL COMMENT '目标ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型：NOTE/COMMENT',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_user_target (user_id, target_id, target_type),
    KEY idx_user_id (user_id),
    KEY idx_target (target_id, target_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 收藏表
CREATE TABLE IF NOT EXISTS t_collect (
    id VARCHAR(32) PRIMARY KEY COMMENT '收藏ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    note_id VARCHAR(32) NOT NULL COMMENT '笔记ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_user_note (user_id, note_id),
    KEY idx_user_id (user_id),
    KEY idx_note_id (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 文件服务相关表
USE small_potato_file;

-- 文件信息表
CREATE TABLE IF NOT EXISTS t_file_info (
    id VARCHAR(32) PRIMARY KEY COMMENT '文件ID',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    url VARCHAR(255) NOT NULL COMMENT '访问URL',
    path VARCHAR(255) NOT NULL COMMENT '存储路径',
    size BIGINT NOT NULL COMMENT '文件大小(字节)',
    type VARCHAR(100) NOT NULL COMMENT '文件类型',
    extension VARCHAR(20) DEFAULT NULL COMMENT '文件扩展名',
    md5 VARCHAR(32) NOT NULL COMMENT '文件MD5',
    user_id VARCHAR(32) NOT NULL COMMENT '上传用户ID',
    file_type VARCHAR(20) NOT NULL COMMENT '文件用途：NOTE_IMAGE/AVATAR/VIDEO',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    KEY idx_user_id (user_id),
    KEY idx_file_type (file_type),
    KEY idx_md5 (md5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件信息表';

-- 搜索服务相关表
USE small_potato_search;

-- 搜索历史表
CREATE TABLE IF NOT EXISTS t_search_history (
    id VARCHAR(32) PRIMARY KEY COMMENT '历史ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    keyword VARCHAR(100) NOT NULL COMMENT '搜索关键词',
    search_type VARCHAR(20) NOT NULL COMMENT '搜索类型：USER/NOTE/TAG',
    count INT DEFAULT 1 COMMENT '搜索次数',
    last_search_time DATETIME NOT NULL COMMENT '最后搜索时间',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_user_keyword_type (user_id, keyword, search_type),
    KEY idx_user_id (user_id),
    KEY idx_last_search_time (last_search_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- 热门搜索表
CREATE TABLE IF NOT EXISTS t_hot_search (
    id VARCHAR(32) PRIMARY KEY COMMENT '热搜ID',
    keyword VARCHAR(100) NOT NULL COMMENT '搜索关键词',
    search_type VARCHAR(20) NOT NULL COMMENT '搜索类型：USER/NOTE/TAG',
    count INT DEFAULT 0 COMMENT '搜索次数',
    score INT DEFAULT 0 COMMENT '热度分数',
    rank INT DEFAULT 0 COMMENT '排名',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_keyword_type (keyword, search_type),
    KEY idx_score (score),
    KEY idx_rank (rank)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门搜索表';

-- 消息服务相关表
USE small_potato_message;

-- 系统通知表
CREATE TABLE IF NOT EXISTS t_system_notification (
    id VARCHAR(32) PRIMARY KEY COMMENT '通知ID',
    user_id VARCHAR(32) NOT NULL COMMENT '接收用户ID',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    type VARCHAR(20) NOT NULL COMMENT '通知类型',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    read_at DATETIME DEFAULT NULL COMMENT '阅读时间',
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- 用户私信表
CREATE TABLE IF NOT EXISTS t_private_message (
    id VARCHAR(32) PRIMARY KEY COMMENT '私信ID',
    from_user_id VARCHAR(32) NOT NULL COMMENT '发送者ID',
    to_user_id VARCHAR(32) NOT NULL COMMENT '接收者ID',
    content TEXT NOT NULL COMMENT '私信内容',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    read_at DATETIME DEFAULT NULL COMMENT '阅读时间',
    KEY idx_from_user_id (from_user_id),
    KEY idx_to_user_id (to_user_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户私信表';

-- 互动通知表
CREATE TABLE IF NOT EXISTS t_interaction_notification (
    id VARCHAR(32) PRIMARY KEY COMMENT '通知ID',
    user_id VARCHAR(32) NOT NULL COMMENT '接收用户ID',
    from_user_id VARCHAR(32) NOT NULL COMMENT '触发用户ID',
    target_id VARCHAR(32) NOT NULL COMMENT '目标ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型：NOTE/COMMENT',
    action_type VARCHAR(20) NOT NULL COMMENT '动作类型：LIKE/COMMENT/COLLECT/FOLLOW/MENTION',
    content VARCHAR(500) DEFAULT NULL COMMENT '通知内容',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    read_at DATETIME DEFAULT NULL COMMENT '阅读时间',
    KEY idx_user_id (user_id),
    KEY idx_from_user_id (from_user_id),
    KEY idx_target (target_id, target_type),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='互动通知表';

-- 推荐服务相关表
USE small_potato_recommend;

-- 用户兴趣标签表
CREATE TABLE IF NOT EXISTS t_user_interest (
    id VARCHAR(32) PRIMARY KEY COMMENT 'ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    tag_id VARCHAR(32) NOT NULL COMMENT '标签ID',
    score FLOAT DEFAULT 0 COMMENT '兴趣分数',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_user_tag (user_id, tag_id),
    KEY idx_user_id (user_id),
    KEY idx_score (score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣标签表';

-- 用户行为记录表
CREATE TABLE IF NOT EXISTS t_user_behavior (
    id VARCHAR(32) PRIMARY KEY COMMENT 'ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    item_id VARCHAR(32) NOT NULL COMMENT '内容ID',
    item_type VARCHAR(20) NOT NULL COMMENT '内容类型：NOTE/USER/TAG',
    behavior_type VARCHAR(20) NOT NULL COMMENT '行为类型：VIEW/LIKE/COLLECT/COMMENT/FOLLOW',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    KEY idx_user_id (user_id),
    KEY idx_item (item_id, item_type),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为记录表';

-- 内容权重表
CREATE TABLE IF NOT EXISTS t_content_weight (
    id VARCHAR(32) PRIMARY KEY COMMENT 'ID',
    content_id VARCHAR(32) NOT NULL COMMENT '内容ID',
    content_type VARCHAR(20) NOT NULL COMMENT '内容类型：NOTE/USER/TAG',
    hot_score FLOAT DEFAULT 0 COMMENT '热度分数',
    quality_score FLOAT DEFAULT 0 COMMENT '质量分数',
    time_decay FLOAT DEFAULT 1 COMMENT '时间衰减因子',
    total_score FLOAT DEFAULT 0 COMMENT '总分数',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_content (content_id, content_type),
    KEY idx_total_score (total_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容权重表';

-- 内容安全服务相关表
USE small_potato_security;

-- 内容审核记录表
CREATE TABLE IF NOT EXISTS t_content_audit (
    id VARCHAR(32) PRIMARY KEY COMMENT '审核ID',
    content_id VARCHAR(32) NOT NULL COMMENT '内容ID',
    content_type VARCHAR(20) NOT NULL COMMENT '内容类型：NOTE/COMMENT/USER_PROFILE',
    audit_type VARCHAR(20) NOT NULL COMMENT '审核类型：AI/HUMAN',
    status VARCHAR(20) NOT NULL COMMENT '状态：PENDING/PASS/REJECT',
    reason VARCHAR(200) DEFAULT NULL COMMENT '拒绝原因',
    ai_result TEXT DEFAULT NULL COMMENT 'AI审核结果',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    KEY idx_content (content_id, content_type),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容审核记录表';

-- 敏感词表
CREATE TABLE IF NOT EXISTS t_sensitive_word (
    id VARCHAR(32) PRIMARY KEY COMMENT '敏感词ID',
    word VARCHAR(50) NOT NULL COMMENT '敏感词',
    category VARCHAR(20) NOT NULL COMMENT '分类',
    level INT NOT NULL COMMENT '级别：1-轻度，2-中度，3-严重',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_word (word)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

-- 后台管理服务相关表
USE small_potato_admin;

-- 管理员表
CREATE TABLE IF NOT EXISTS t_admin (
    id VARCHAR(32) PRIMARY KEY COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    role VARCHAR(20) NOT NULL COMMENT '角色：SUPER_ADMIN/ADMIN/AUDITOR',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS t_operation_log (
    id VARCHAR(32) PRIMARY KEY COMMENT '日志ID',
    admin_id VARCHAR(32) NOT NULL COMMENT '管理员ID',
    module VARCHAR(50) NOT NULL COMMENT '模块',
    operation VARCHAR(50) NOT NULL COMMENT '操作',
    method VARCHAR(200) NOT NULL COMMENT '方法',
    params TEXT DEFAULT NULL COMMENT '请求参数',
    ip VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    KEY idx_admin_id (admin_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 举报记录表
CREATE TABLE IF NOT EXISTS t_report (
    id VARCHAR(32) PRIMARY KEY COMMENT '举报ID',
    user_id VARCHAR(32) NOT NULL COMMENT '举报用户ID',
    target_id VARCHAR(32) NOT NULL COMMENT '目标ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型：NOTE/COMMENT/USER',
    reason VARCHAR(20) NOT NULL COMMENT '举报原因',
    description VARCHAR(500) DEFAULT NULL COMMENT '详细描述',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/PROCESSED/IGNORED',
    handler_id VARCHAR(32) DEFAULT NULL COMMENT '处理人ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    KEY idx_user_id (user_id),
    KEY idx_target (target_id, target_type),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报记录表'; 
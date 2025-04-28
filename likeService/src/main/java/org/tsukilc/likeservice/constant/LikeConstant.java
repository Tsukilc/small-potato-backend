package org.tsukilc.likeservice.constant;

/**
 * 点赞模块常量类
 */
public class LikeConstant {

    /**
     * 点赞记录缓存前缀
     */
    public static final String LIKE_RECORD_KEY_PREFIX = "like:record:";

    /**
     * 点赞数缓存前缀
     */
    public static final String LIKE_COUNT_KEY_PREFIX = "like:count:";

    /**
     * 用户点赞列表缓存前缀
     */
    public static final String USER_LIKE_LIST_KEY_PREFIX = "like:user:list:";

    /**
     * 用户笔记总点赞数缓存前缀
     */
    public static final String USER_TOTAL_LIKE_COUNT_KEY_PREFIX = "like:user:total:";

    /**
     * 点赞记录缓存过期时间（小时）
     */
    public static final int LIKE_RECORD_EXPIRE_HOURS = 24;

    /**
     * 点赞数缓存过期时间（小时）
     */
    public static final int LIKE_COUNT_EXPIRE_HOURS = 48;

    /**
     * 用户点赞列表缓存过期时间（小时）
     */
    public static final int USER_LIKE_LIST_EXPIRE_HOURS = 24;

    /**
     * 用户笔记总点赞数缓存过期时间（小时）
     */
    public static final int USER_TOTAL_LIKE_COUNT_EXPIRE_HOURS = 48;

    /**
     * 待处理的点赞记录缓存键
     */
    public static final String PENDING_LIKE_RECORDS_KEY = "like:pending:records";

    /**
     * 待处理的点赞数变更缓存键
     */
    public static final String PENDING_LIKE_COUNTS_KEY = "like:pending:counts";

    /**
     * 数据持久化间隔时间(秒)
     */
    public static final int PERSISTENCE_INTERVAL_SECONDS = 10;
}
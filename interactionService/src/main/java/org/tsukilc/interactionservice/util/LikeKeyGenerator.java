package org.tsukilc.interactionservice.util;

import org.tsukilc.interactionservice.constant.LikeConstant;

/**
 * 点赞缓存Key生成工具
 */
public class LikeKeyGenerator {
    
    /**
     * 生成点赞记录缓存Key
     *
     * @param userId   用户ID
     * @param targetId 目标ID
     * @param type     点赞类型
     * @return 缓存Key
     */
    public static String generateLikeRecordKey(Long userId, Long targetId, Integer type) {
        return LikeConstant.LIKE_RECORD_KEY_PREFIX + userId + ":" + targetId + ":" + type;
    }
    
    /**
     * 生成点赞数缓存Key
     *
     * @param targetId 目标ID
     * @param type     点赞类型
     * @return 缓存Key
     */
    public static String generateLikeCountKey(Long targetId, Integer type) {
        return LikeConstant.LIKE_COUNT_KEY_PREFIX + targetId + ":" + type;
    }
    
    /**
     * 生成用户点赞列表缓存Key
     *
     * @param userId 用户ID
     * @param type   点赞类型
     * @return 缓存Key
     */
    public static String generateUserLikeListKey(Long userId, Integer type) {
        return LikeConstant.USER_LIKE_LIST_KEY_PREFIX + userId + ":" + type;
    }
    
    /**
     * 生成用户笔记总点赞数缓存Key
     *
     * @param userId 用户ID
     * @return 缓存Key
     */
    public static String generateUserTotalLikeCountKey(Long userId) {
        return LikeConstant.USER_TOTAL_LIKE_COUNT_KEY_PREFIX + userId;
    }
} 
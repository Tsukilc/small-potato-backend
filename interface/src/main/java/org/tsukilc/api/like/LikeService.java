package org.tsukilc.api.like;


import org.tsukilc.api.like.dto.LikeCountRequest;
import org.tsukilc.api.like.dto.LikeRequest;
import org.tsukilc.api.like.dto.LikeResponse;
import org.tsukilc.api.like.dto.UserLikeListRequest;

import java.util.List;
import java.util.Map;

/**
 * 点赞服务接口
 * 平台化设计：提供统一的点赞服务接口，支持不同业务类型的点赞
 */
public interface LikeService {

    /**
     * 点赞或取消点赞
     *
     * @param request 点赞请求
     * @return 点赞结果
     */
    LikeResponse like(LikeRequest request);

    /**
     * 获取用户对特定目标的点赞状态
     *
     * @param userId   用户ID
     * @param targetId 目标ID
     * @param type     点赞类型
     * @return 点赞状态
     */
    Boolean getLikeStatus(Long userId, Long targetId, Integer type);

    /**
     * 获取用户的点赞列表
     *
     * @param request 查询请求
     * @return 点赞列表
     */
    List<LikeResponse> getUserLikeList(UserLikeListRequest request);

    /**
     * 批量获取目标的点赞数量
     *
     * @param request 查询请求
     * @return 目标ID到点赞数量的映射
     */
    Map<Long, Long> batchGetLikeCount(LikeCountRequest request);

    /**
     * 获取用户所有笔记的总点赞数
     *
     * @param userId 用户ID
     * @return 总点赞数
     */
    Long getUserTotalLikeCount(Long userId);
} 
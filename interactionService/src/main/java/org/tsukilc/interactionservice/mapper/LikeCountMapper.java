package org.tsukilc.interactionservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.tsukilc.interactionservice.entity.LikeCount;

import java.util.List;
import java.util.Map;

/**
 * 点赞计数Mapper接口
 */
@Mapper
public interface LikeCountMapper extends BaseMapper<LikeCount> {
    
    /**
     * 增加点赞数
     *
     * @param targetId 目标ID
     * @param type     点赞类型
     * @param step     增加步长，可为负数表示减少
     * @return 影响行数
     */
    int incrementCount(@Param("targetId") Long targetId, @Param("type") Integer type, @Param("step") Long step);
    
    /**
     * 批量获取点赞数
     *
     * @param targetIds 目标ID列表
     * @param type      点赞类型
     * @return 目标ID到点赞数的映射
     */
    List<Map<String, Object>> batchGetLikeCount(@Param("targetIds") List<Long> targetIds, @Param("type") Integer type);
    
    /**
     * 获取用户所有笔记的总点赞数
     *
     * @param userId 用户ID
     * @return 总点赞数
     */
    Long getUserTotalLikeCount(@Param("userId") Long userId);
} 
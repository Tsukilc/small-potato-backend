package org.tsukilc.likeservice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tsukilc.api.like.LikeService;
import org.tsukilc.api.like.dto.LikeCountRequest;
import org.tsukilc.api.like.dto.LikeRequest;
import org.tsukilc.api.like.dto.LikeResponse;
import org.tsukilc.api.like.dto.UserLikeListRequest;
import org.tsukilc.common.service.CacheService;
import org.tsukilc.likeservice.constant.LikeConstant;
import org.tsukilc.likeservice.dto.LikeMessage;
import org.tsukilc.likeservice.entity.LikeCount;
import org.tsukilc.likeservice.entity.LikeRecord;
import org.tsukilc.likeservice.mapper.LikeCountMapper;
import org.tsukilc.likeservice.mapper.LikeRecordMapper;
import org.tsukilc.likeservice.util.LikeKeyGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 点赞服务实现类
 */
@Slf4j
@Service
@DubboService
public class LikeServiceImpl implements LikeService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private LikeCountMapper likeCountMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public LikeResponse like(LikeRequest request) {
        if (request.getUserId() == null || request.getTargetId() == null || request.getType() == null) {
            throw new IllegalArgumentException("用户ID、目标ID和类型不能为空");
        }

        // 生成缓存Key
        String likeRecordKey = LikeKeyGenerator.generateLikeRecordKey(
                request.getUserId(), request.getTargetId(), request.getType());
        String likeCountKey = LikeKeyGenerator.generateLikeCountKey(
                request.getTargetId(), request.getType());

        // 从缓存中获取点赞状态
        Boolean oldStatus = cacheService.get(likeRecordKey, Boolean.class);

        // 如果操作状态和当前状态一致，则直接返回
        if (oldStatus != null && oldStatus.equals(request.getStatus())) {
            return LikeResponse.builder()
                    .userId(request.getUserId())
                    .targetId(request.getTargetId())
                    .type(request.getType())
                    .liked(request.getStatus())
                    .count(getCount(request.getTargetId(), request.getType()))
                    .createTime(LocalDateTime.now())
                    .build();
        }

        // 更新缓存中的点赞状态
        cacheService.set(likeRecordKey, request.getStatus(),
                LikeConstant.LIKE_RECORD_EXPIRE_HOURS, TimeUnit.HOURS);

        // 更新缓存中的点赞数量
        Long count = cacheService.get(likeCountKey, Long.class);
        if (count == null) {
            // 如果缓存中没有，则查询数据库
            LambdaQueryWrapper<LikeCount> countWrapper = new LambdaQueryWrapper<>();
            countWrapper.eq(LikeCount::getTargetId, request.getTargetId())
                    .eq(LikeCount::getType, request.getType())
                    .last("LIMIT 1");

            LikeCount likeCount = likeCountMapper.selectOne(countWrapper);
            count = likeCount != null ? likeCount.getCount() : 0L;
        }

        // 计算步长
        long step = request.getStatus() ? 1L : -1L;

        // 更新缓存中的点赞数
        count = Math.max(0, count + step);
        cacheService.set(likeCountKey, count,
                LikeConstant.LIKE_COUNT_EXPIRE_HOURS, TimeUnit.HOURS);

        // 创建点赞消息并添加到待处理列表
        LikeMessage message = LikeMessage.builder()
                .userId(request.getUserId())
                .targetId(request.getTargetId())
                .type(request.getType())
                .status(request.getStatus())
                .operateTime(LocalDateTime.now())
                .build();

        try {
            // 序列化消息为JSON
            String messageJson = objectMapper.writeValueAsString(message);

            // 添加到待处理的点赞记录列表
            cacheService.lPush(LikeConstant.PENDING_LIKE_RECORDS_KEY, messageJson);

            // 累加到待处理的点赞计数变更
            String countKey = request.getTargetId() + ":" + request.getType();
            cacheService.hIncrBy(LikeConstant.PENDING_LIKE_COUNTS_KEY, countKey, step);
        } catch (JsonProcessingException e) {
            log.error("序列化点赞消息失败", e);
        }

        // 清除用户点赞列表缓存
        String userLikeListKey = LikeKeyGenerator.generateUserLikeListKey(
                request.getUserId(), request.getType());
        cacheService.delete(userLikeListKey);

        // 如果是文章类型的点赞，清除用户总点赞数缓存
        if (request.getType() == 1) { // 假设1是文章类型
            String userTotalLikeCountKey = LikeKeyGenerator.generateUserTotalLikeCountKey(request.getUserId());
            cacheService.delete(userTotalLikeCountKey);
        }

        return LikeResponse.builder()
                .userId(request.getUserId())
                .targetId(request.getTargetId())
                .type(request.getType())
                .liked(request.getStatus())
                .count(count)
                .createTime(LocalDateTime.now())
                .build();
    }

    @Override
    public Boolean getLikeStatus(Long userId, Long targetId, Integer type) {
        // 生成缓存Key
        String likeRecordKey = LikeKeyGenerator.generateLikeRecordKey(userId, targetId, type);

        // 从缓存中获取点赞状态
        Boolean status = cacheService.get(likeRecordKey, Boolean.class);

        // 如果缓存中没有，则查询数据库
        if (status == null) {
            LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LikeRecord::getUserId, userId)
                    .eq(LikeRecord::getTargetId, targetId)
                    .eq(LikeRecord::getType, type)
                    .last("LIMIT 1");

            LikeRecord record = likeRecordMapper.selectOne(wrapper);
            status = record != null && record.getStatus() == 1;

            // 将结果放入缓存
            cacheService.set(likeRecordKey, status,
                    LikeConstant.LIKE_RECORD_EXPIRE_HOURS, TimeUnit.HOURS);
        }

        return status;
    }

    @Override
    public List<LikeResponse> getUserLikeList(UserLikeListRequest request) {
        // 生成缓存Key
        String userLikeListKey = LikeKeyGenerator.generateUserLikeListKey(
                request.getUserId(), request.getType());

        // 从缓存获取用户点赞列表（最近点赞的记录，使用ZSet存储）
        // 这里我们仅获取缓存，完整实现应该支持分页和更多数据
        List<LikeResponse> resultList = new ArrayList<>();

        // TODO: 实现ZSet存储用户最近点赞列表，这里简化为直接查询数据库

        // 查询数据库
        Page<LikeRecord> page = new Page<>(
                request.getPageNum() != null ? request.getPageNum() : 1,
                request.getPageSize() != null ? request.getPageSize() : 10
        );

        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getUserId, request.getUserId())
                .eq(request.getType() != null, LikeRecord::getType, request.getType())
                .eq(LikeRecord::getStatus, 1)
                .orderByDesc(LikeRecord::getCreateTime);

        Page<LikeRecord> result = likeRecordMapper.selectPage(page, wrapper);

        // 将查询结果转换为DTO
        return result.getRecords().stream().map(record -> {
            Long count = getCount(record.getTargetId(), record.getType());

            return LikeResponse.builder()
                    .userId(record.getUserId())
                    .targetId(record.getTargetId())
                    .type(record.getType())
                    .liked(record.getStatus() == 1)
                    .count(count)
                    .createTime(record.getCreateTime())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> batchGetLikeCount(LikeCountRequest request) {
        Map<Long, Long> result = new HashMap<>();
        List<Long> targetIds = request.getTargetIds();

        if (targetIds == null || targetIds.isEmpty()) {
            return result;
        }

        // 批量从缓存中获取点赞数
        List<String> keys = targetIds.stream()
                .map(targetId -> LikeKeyGenerator.generateLikeCountKey(targetId, request.getType()))
                .collect(Collectors.toList());

        List<Long> counts = cacheService.multiGet(keys, Long.class);

        // 创建需要从数据库查询的targetId列表
        List<Long> missedTargetIds = new ArrayList<>();

        // 处理缓存命中的结果
        for (int i = 0; i < targetIds.size(); i++) {
            Long targetId = targetIds.get(i);
            Long count = i < counts.size() ? counts.get(i) : null;

            if (count != null) {
                result.put(targetId, count);
            } else {
                missedTargetIds.add(targetId);
            }
        }

        // 如果有未在缓存中找到的targetId，则从数据库查询
        if (!missedTargetIds.isEmpty()) {
            List<Map<String, Object>> dbResults = likeCountMapper.batchGetLikeCount(
                    missedTargetIds, request.getType());

            // 将数据库查询结果放入缓存和结果Map
            for (Map<String, Object> dbResult : dbResults) {
                Long targetId = ((Number) dbResult.get("target_id")).longValue();
                Long count = ((Number) dbResult.get("count")).longValue();

                // 放入结果Map
                result.put(targetId, count);

                // 放入缓存
                String countKey = LikeKeyGenerator.generateLikeCountKey(targetId, request.getType());
                cacheService.set(countKey, count,
                        LikeConstant.LIKE_COUNT_EXPIRE_HOURS, TimeUnit.HOURS);
            }

            // 对于数据库中也没有的记录，设置为0
            for (Long targetId : missedTargetIds) {
                if (!result.containsKey(targetId)) {
                    result.put(targetId, 0L);

                    // 放入缓存
                    String countKey = LikeKeyGenerator.generateLikeCountKey(targetId, request.getType());
                    cacheService.set(countKey, 0L,
                            LikeConstant.LIKE_COUNT_EXPIRE_HOURS, TimeUnit.HOURS);
                }
            }
        }

        return result;
    }

    @Override
    public Long getUserTotalLikeCount(Long userId) {
        // 生成缓存Key
        String userTotalLikeCountKey = LikeKeyGenerator.generateUserTotalLikeCountKey(userId);

        // 从缓存中获取用户文章总点赞数
        Long totalCount = cacheService.get(userTotalLikeCountKey, Long.class);

        // 如果缓存中没有，则查询数据库
        if (totalCount == null) {
            totalCount = likeCountMapper.getUserTotalLikeCount(userId);

            // 将结果放入缓存
            cacheService.set(userTotalLikeCountKey, totalCount,
                    LikeConstant.USER_TOTAL_LIKE_COUNT_EXPIRE_HOURS, TimeUnit.HOURS);
        }

        return totalCount;
    }

    /**
     * 获取目标的点赞数量
     */
    private Long getCount(Long targetId, Integer type) {
        String likeCountKey = LikeKeyGenerator.generateLikeCountKey(targetId, type);
        Long count = cacheService.get(likeCountKey, Long.class);

        if (count == null) {
            LambdaQueryWrapper<LikeCount> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LikeCount::getTargetId, targetId)
                    .eq(LikeCount::getType, type)
                    .last("LIMIT 1");

            LikeCount likeCount = likeCountMapper.selectOne(wrapper);
            count = likeCount != null ? likeCount.getCount() : 0L;

            // 将结果放入缓存
            cacheService.set(likeCountKey, count,
                    LikeConstant.LIKE_COUNT_EXPIRE_HOURS, TimeUnit.HOURS);
        }

        return count;
    }
}
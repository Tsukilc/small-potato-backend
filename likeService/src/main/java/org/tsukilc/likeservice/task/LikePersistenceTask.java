package org.tsukilc.likeservice.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tsukilc.common.service.CacheService;
import org.tsukilc.likeservice.constant.LikeConstant;
import org.tsukilc.likeservice.dto.LikeMessage;
import org.tsukilc.likeservice.entity.LikeRecord;
import org.tsukilc.likeservice.mapper.LikeCountMapper;
import org.tsukilc.likeservice.mapper.LikeRecordMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点赞数据持久化定时任务
 * 每隔一段时间将缓存中的点赞数据持久化到数据库
 */
@Slf4j
@Component
@EnableScheduling
public class LikePersistenceTask {

    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    
    @Autowired
    private LikeCountMapper likeCountMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 定时任务：每隔一段时间将缓存中的点赞数据持久化到数据库
     * 默认每10秒执行一次
     */
    @Scheduled(fixedDelayString = "${like.persistence.interval:10000}")
    @Transactional(rollbackFor = Exception.class)
    public void persistLikeData() {
        log.info("开始执行点赞数据持久化任务");
        try {
            // 处理点赞记录
            processPendingLikeRecords();
            
            // 处理点赞计数
            processPendingLikeCounts();
            
            log.info("点赞数据持久化任务执行完成");
        } catch (Exception e) {
            log.error("点赞数据持久化任务执行异常", e);
        }
    }
    
    /**
     * 处理待持久化的点赞记录
     */
    private void processPendingLikeRecords() {
        // 从缓存中获取所有待处理的点赞记录
        List<String> pendingRecords = cacheService.lRange(
                LikeConstant.PENDING_LIKE_RECORDS_KEY, 0, -1);
        
        if (pendingRecords == null || pendingRecords.isEmpty()) {
            return;
        }
        
        log.info("开始处理{}条待持久化的点赞记录", pendingRecords.size());
        
        // 按用户ID和目标ID分组，保留最新的操作
        Map<String, LikeMessage> latestOperations = new HashMap<>();
        
        for (String recordJson : pendingRecords) {
            try {
                LikeMessage message = objectMapper.readValue(recordJson, LikeMessage.class);
                String key = message.getUserId() + ":" + message.getTargetId() + ":" + message.getType();
                
                // 如果已存在相同用户对相同目标的操作，保留最新的一条
                LikeMessage existingMessage = latestOperations.get(key);
                if (existingMessage == null ||
                    message.getOperateTime().isAfter(existingMessage.getOperateTime())) {
                    latestOperations.put(key, message);
                }
            } catch (JsonProcessingException e) {
                log.error("解析点赞记录JSON异常: {}", recordJson, e);
            }
        }
        
        // 将最新的操作应用到数据库
        List<LikeRecord> recordsToInsert = new ArrayList<>();

        // todo：研究一下批量插入&改成消息队列异步插入数据库
        for (LikeMessage message : latestOperations.values()) {
            // 查询是否已存在记录
            LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LikeRecord::getUserId, message.getUserId())
                  .eq(LikeRecord::getTargetId, message.getTargetId())
                  .eq(LikeRecord::getType, message.getType())
                  .last("LIMIT 1");
            
            LikeRecord existingRecord = likeRecordMapper.selectOne(wrapper);
            
            if (existingRecord != null) {
                // 更新现有记录
                if (existingRecord.getStatus() != (message.getStatus() ? 1 : 0)) {
                    existingRecord.setStatus(message.getStatus() ? 1 : 0);
                    existingRecord.setUpdateTime(LocalDateTime.now());
                    likeRecordMapper.updateById(existingRecord);
                }
            } else {
                // 创建新记录
                LikeRecord newRecord = new LikeRecord();
                newRecord.setUserId(message.getUserId());
                newRecord.setTargetId(message.getTargetId());
                newRecord.setType(message.getType());
                newRecord.setStatus(message.getStatus() ? 1 : 0);
                newRecord.setCreateTime(message.getOperateTime());
                newRecord.setUpdateTime(message.getOperateTime());
                recordsToInsert.add(newRecord);
            }
        }
        
        // 批量插入新记录
        if (!recordsToInsert.isEmpty()) {
            likeRecordMapper.batchInsert(recordsToInsert);
        }
        
        // 清空待处理的点赞记录
        cacheService.delete(LikeConstant.PENDING_LIKE_RECORDS_KEY);
        
        log.info("完成处理待持久化的点赞记录，更新: {}条，新增: {}条",
                latestOperations.size() - recordsToInsert.size(), recordsToInsert.size());
    }
    
    /**
     * 处理待持久化的点赞计数
     */
    private void processPendingLikeCounts() {
        // 从缓存中获取所有待处理的点赞计数
        Map<String, String> pendingCounts = cacheService.GetAll(LikeConstant.PENDING_LIKE_COUNTS_KEY);
        
        if (pendingCounts == null || pendingCounts.isEmpty()) {
            return;
        }
        
        log.info("开始处理{}条待持久化的点赞计数", pendingCounts.size());
        
        for (Map.Entry<String, String> entry : pendingCounts.entrySet()) {
            try {
                // 解析键（目标ID:类型）
                String[] keyParts = entry.getKey().split(":");
                Long targetId = Long.parseLong(keyParts[0]);
                Integer type = Integer.parseInt(keyParts[1]);
                
                // 解析变更的步长
                Long step = Long.parseLong(entry.getValue());
                
                // 更新数据库中的点赞计数
                if (step != 0) {
                    likeCountMapper.incrementCount(targetId, type, step);
                }
            } catch (Exception e) {
                log.error("处理点赞计数异常: key={}, value={}", 
                        entry.getKey(), entry.getValue(), e);
            }
        }
        
        // 清空待处理的点赞计数
        cacheService.delete(LikeConstant.PENDING_LIKE_COUNTS_KEY);
        
        log.info("完成处理待持久化的点赞计数");
    }
}
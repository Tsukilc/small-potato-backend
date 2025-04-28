package org.tsukilc.interactionservice.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tsukilc.common.constant.MQConstant;
import org.tsukilc.interactionservice.dto.LikeMessage;
import org.tsukilc.interactionservice.entity.LikeCount;
import org.tsukilc.interactionservice.entity.LikeRecord;
import org.tsukilc.interactionservice.mapper.LikeCountMapper;
import org.tsukilc.interactionservice.mapper.LikeRecordMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 点赞消息消费者
 * 处理点赞和取消点赞的异步消息
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQConstant.LIKE_TOPIC,
        consumerGroup = "like-consumer-group"
)
public class LikeConsumer implements RocketMQListener<String> {

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private LikeCountMapper likeCountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(String message) {
        log.info("收到点赞消息: {}", message);
        
        try {
            // 解析消息
            LikeMessage likeMessage = JSON.parseObject(message, LikeMessage.class);
            if (likeMessage == null) {
                log.error("解析点赞消息失败: {}", message);
                return;
            }
            
            // 处理点赞记录
            processLikeRecord(likeMessage);
            
            // 处理点赞计数
            processLikeCount(likeMessage);
            
            log.info("处理点赞消息成功: {}", message);
        } catch (Exception e) {
            log.error("处理点赞消息异常: {}", message, e);
            throw e; // 抛出异常以便事务回滚
        }
    }
    
    /**
     * 处理点赞记录
     */
    private void processLikeRecord(LikeMessage likeMessage) {
        // 查询是否已存在点赞记录
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getUserId, likeMessage.getUserId())
              .eq(LikeRecord::getTargetId, likeMessage.getTargetId())
              .eq(LikeRecord::getType, likeMessage.getType())
              .last("LIMIT 1");
        
        LikeRecord existRecord = likeRecordMapper.selectOne(wrapper);
        
        if (existRecord == null) {
            // 如果不存在，则创建新的点赞记录
            LikeRecord newRecord = new LikeRecord();
            newRecord.setUserId(likeMessage.getUserId());
            newRecord.setTargetId(likeMessage.getTargetId());
            newRecord.setType(likeMessage.getType());
            newRecord.setStatus(likeMessage.getStatus());
            newRecord.setCreateTime(likeMessage.getOperateTime());
            newRecord.setUpdateTime(likeMessage.getOperateTime());
            
            // 批量插入点赞记录（虽然只有一条，但使用批量接口以便将来扩展）
            List<LikeRecord> records = new ArrayList<>();
            records.add(newRecord);
            likeRecordMapper.batchInsert(records);
        } else {
            // 如果已存在，则更新点赞状态
            existRecord.setStatus(likeMessage.getStatus());
            existRecord.setUpdateTime(likeMessage.getOperateTime());
            likeRecordMapper.updateById(existRecord);
        }
    }
    
    /**
     * 处理点赞计数
     */
    private void processLikeCount(LikeMessage likeMessage) {
        // 计算步长
        long step = likeMessage.getStatus() ? 1L : -1L;
        
        // 直接使用Mapper的incrementCount方法更新点赞计数
        // 如果记录不存在，此方法会自动创建记录并设置初始值；如果已存在，则增加或减少数量
        int affected = likeCountMapper.incrementCount(
                likeMessage.getTargetId(),
                likeMessage.getType(),
                step
        );
        
        // 如果没有影响任何记录，且是点赞操作，则需要创建一条新记录
        if (affected == 0 && likeMessage.getStatus()) {
            LikeCount likeCount = new LikeCount();
            likeCount.setTargetId(likeMessage.getTargetId());
            likeCount.setType(likeMessage.getType());
            likeCount.setCount(1L); // 初始值为1
            likeCount.setCreateTime(likeMessage.getOperateTime());
            likeCount.setUpdateTime(likeMessage.getOperateTime());
            likeCountMapper.insert(likeCount);
        }
    }
} 
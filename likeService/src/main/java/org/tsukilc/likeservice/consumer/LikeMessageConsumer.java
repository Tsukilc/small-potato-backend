//package org.tsukilc.likeservice.consumer;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.apis.ClientConfiguration;
//import org.apache.rocketmq.client.apis.ClientException;
//import org.apache.rocketmq.client.apis.ClientServiceProvider;
//import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
//import org.apache.rocketmq.client.apis.consumer.FilterExpression;
//import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
//import org.apache.rocketmq.client.apis.consumer.PushConsumer;
//import org.apache.rocketmq.client.apis.message.MessageId;
//import org.apache.rocketmq.client.apis.message.MessageView;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.tsukilc.common.constant.MQConstant;
//import org.tsukilc.likeservice.dto.LikeMessage;
//import org.tsukilc.likeservice.entity.LikeRecord;
//import org.tsukilc.likeservice.mapper.LikeCountMapper;
//import org.tsukilc.likeservice.mapper.LikeRecordMapper;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * 点赞消息消费者
// * 负责异步处理点赞和取消点赞的消息，批量写入数据库
// */
//@Slf4j
//@Component
//public class LikeMessageConsumer {
//
//    @Autowired
//    private LikeRecordMapper likeRecordMapper;
//
//    @Autowired
//    private LikeCountMapper likeCountMapper;
//
//    /**
//     * 消息缓冲区，用于聚合写入
//     */
//    private final ConcurrentHashMap<String, List<LikeMessage>> messageBuffer = new ConcurrentHashMap<>();
//
//    /**
//     * 消息计数器，用于触发批量写入
//     */
//    private final AtomicInteger messageCounter = new AtomicInteger(0);
//
//    /**
//     * 定时调度器，用于定时批量写入
//     */
//    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
//
//    /**
//     * JSON序列化工具
//     */
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    /**
//     * RocketMQ消费者
//     */
//    private PushConsumer pushConsumer;
//
//    /**
//     * 初始化消费者
//     */
//    @PostConstruct
//    public void init() {
//        try {
//            // 获取客户端服务提供者
//            ClientServiceProvider provider = ClientServiceProvider.loadService();
//
//            // 创建消费者过滤表达式
//            Map<String, FilterExpression> filterExpressionMap = new HashMap<>();
//            filterExpressionMap.put(MQConstant.LIKE_TOPIC,
//                    new FilterExpression("*", FilterExpressionType.TAG));
//
//            // 创建客户端配置
//            ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
//                    .setEndpoints(MQConstant.NAME_SERVER_ADDRESS)
//                    .build();
//
//            // 创建消费者并订阅主题
//            pushConsumer = provider.newPushConsumerBuilder()
//                    .setClientConfiguration(clientConfiguration)
//                    .setConsumerGroup(MQConstant.CONSUMER_GROUP)
//                    .setSubscriptionExpressions(filterExpressionMap)
//                    // 设置消费者线程数
//                    .setConsumptionThreadCount(20)
//                    // 注册消息监听器
//                    .setMessageListener(messageView -> {
//                        try {
//                            return processMessage(messageView);
//                        } catch (Exception e) {
//                            log.error("处理消息失败", e);
//                            return ConsumeResult.FAILURE;
//                        }
//                    })
//                    .build();
//
//            // 启动定时任务，周期性刷新缓冲区
//            scheduler.scheduleAtFixedRate(
//                    this::flushAllMessages,
//                    5000,  // 初始延迟5秒
//                    5000,  // 周期5秒
//                    TimeUnit.MILLISECONDS
//            );
//
//            log.info("点赞消息消费者初始化成功");
//        } catch (ClientException e) {
//            log.error("初始化RocketMQ消费者失败", e);
//            throw new RuntimeException("初始化RocketMQ消费者失败", e);
//        }
//    }
//
//    /**
//     * 处理接收到的消息
//     *
//     * @param messageView 消息视图
//     * @return 消费结果
//     */
//    private ConsumeResult processMessage(MessageView messageView) {
//        try {
//            // 获取消息ID
//            MessageId messageId = messageView.getMessageId();
//            // 获取消息标签
//            String tag = messageView.getTag().orElse("");
//            // 获取消息体
//            byte[] body = messageView.getBody().array();
//            String messageContent = new String(body, StandardCharsets.UTF_8);
//
//            // 反序列化消息内容
//            LikeMessage likeMessage = objectMapper.readValue(messageContent, LikeMessage.class);
//
//            log.info("收到点赞消息, ID: {}, Tag: {}", messageId, tag);
//
//            // 根据标签类型将消息放入不同的缓冲区
//            String bufferKey = MQConstant.LIKE_TAG.equals(tag) ? "like" : "unlike";
//            messageBuffer.computeIfAbsent(bufferKey, k -> new ArrayList<>()).add(likeMessage);
//
//            // 增加消息计数
//            int count = messageCounter.incrementAndGet();
//
//            // 当消息数量达到100条时，执行批量处理
//            if (count >= 100) {
//                flushAllMessages();
//            }
//
//            return ConsumeResult.SUCCESS;
//        } catch (JsonProcessingException e) {
//            log.error("解析消息失败", e);
//            return ConsumeResult.FAILURE;
//        }
//    }
//
//    /**
//     * 刷新所有缓冲区中的消息
//     */
//    private synchronized void flushAllMessages() {
//        try {
//            // 点赞消息批量处理
//            List<LikeMessage> likeMessages = messageBuffer.remove("like");
//            if (likeMessages != null && !likeMessages.isEmpty()) {
//                processLikeMessages(likeMessages);
//            }
//
//            // 取消点赞消息批量处理
//            List<LikeMessage> unlikeMessages = messageBuffer.remove("unlike");
//            if (unlikeMessages != null && !unlikeMessages.isEmpty()) {
//                processUnlikeMessages(unlikeMessages);
//            }
//
//            // 重置计数器
//            messageCounter.set(0);
//        } catch (Exception e) {
//            log.error("刷新点赞消息失败", e);
//        }
//    }
//
//    /**
//     * 批量处理点赞消息
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void processLikeMessages(List<LikeMessage> messages) {
//        if (messages.isEmpty()) {
//            return;
//        }
//
//        // 批量插入点赞记录
//        List<LikeRecord> records = new ArrayList<>(messages.size());
//        for (LikeMessage message : messages) {
//            // 先查询是否已存在记录
//            LikeRecord existingRecord = likeRecordMapper.selectOne(
//                    new LambdaQueryWrapper<LikeRecord>()
//                            .eq(LikeRecord::getUserId, message.getUserId())
//                            .eq(LikeRecord::getTargetId, message.getTargetId())
//                            .eq(LikeRecord::getType, message.getType())
//                            .last("LIMIT 1")
//            );
//
//            if (existingRecord != null) {
//                // 如果已存在记录且状态不同，则更新状态
//                if (!existingRecord.getStatus().equals(message.getStatus())) {
//                    existingRecord.setStatus(message.getStatus());
//                    existingRecord.setUpdateTime(LocalDateTime.now());
//                    likeRecordMapper.updateById(existingRecord);
//                }
//            } else {
//                // 如果不存在记录，则准备插入
//                LikeRecord record = LikeRecord.builder()
//                        .userId(message.getUserId())
//                        .targetId(message.getTargetId())
//                        .type(message.getType())
//                        .status(message.getStatus())
//                        .createTime(message.getOperateTime())
//                        .updateTime(message.getOperateTime())
//                        .build();
//                records.add(record);
//            }
//
//            // 更新点赞数
//            likeCountMapper.incrementCount(message.getTargetId(), message.getType(), 1L);
//        }
//
//        // 批量插入新记录
//        if (!records.isEmpty()) {
//            likeRecordMapper.batchInsert(records);
//        }
//
//        log.info("批量处理点赞消息完成，数量：{}", messages.size());
//    }
//
//    /**
//     * 批量处理取消点赞消息
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void processUnlikeMessages(List<LikeMessage> messages) {
//        if (messages.isEmpty()) {
//            return;
//        }
//
//        for (LikeMessage message : messages) {
//            // 查询是否已存在记录
//            LikeRecord existingRecord = likeRecordMapper.selectOne(
//                    new LambdaQueryWrapper<LikeRecord>()
//                            .eq(LikeRecord::getUserId, message.getUserId())
//                            .eq(LikeRecord::getTargetId, message.getTargetId())
//                            .eq(LikeRecord::getType, message.getType())
//                            .last("LIMIT 1")
//            );
//
//            if (existingRecord != null) {
//                // 更新点赞状态为取消
//                existingRecord.setStatus(false);
//                existingRecord.setUpdateTime(LocalDateTime.now());
//                likeRecordMapper.updateById(existingRecord);
//
//                // 更新点赞数
//                likeCountMapper.incrementCount(message.getTargetId(), message.getType(), -1L);
//            }
//        }
//
//        log.info("批量处理取消点赞消息完成，数量：{}", messages.size());
//    }
//
//    /**
//     * 销毁消费者
//     */
//    @PreDestroy
//    public void destroy() {
//        try {
//            // 关闭调度器
//            if (scheduler != null) {
//                scheduler.shutdown();
//            }
//
//            // 关闭消费者
//            if (pushConsumer != null) {
//                pushConsumer.close();
//            }
//
//            log.info("点赞消息消费者销毁成功");
//        } catch (Exception e) {
//            log.error("销毁点赞消息消费者失败", e);
//        }
//    }
//}
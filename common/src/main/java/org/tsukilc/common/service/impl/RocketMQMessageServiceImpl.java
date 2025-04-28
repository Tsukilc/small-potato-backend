//package org.tsukilc.common.service.impl;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.apis.ClientException;
//import org.apache.rocketmq.client.apis.message.Message;
//import org.apache.rocketmq.client.apis.message.MessageBuilder;
//import org.apache.rocketmq.client.apis.producer.Producer;
//import org.apache.rocketmq.client.apis.producer.SendReceipt;
//import org.apache.rocketmq.client.java.message.MessageBuilderImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.tsukilc.common.constant.MQConstant;
//import org.tsukilc.common.service.MessageService;
//
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.Optional;
//import java.util.concurrent.CompletableFuture;
//
///**
// * RocketMQ消息服务实现
// * 平台化设计：提供标准化的消息发送服务，支持异步消息处理
// */
//@Slf4j
//@Service
//public class RocketMQMessageServiceImpl implements MessageService {
//
//    private final Producer producer;
//    private final ObjectMapper objectMapper;
//
//    @Autowired
//    public RocketMQMessageServiceImpl(Producer producer) {
//        this.producer = producer;
//        this.objectMapper = new ObjectMapper();
//    }
//
//    @Override
//    public String syncSend(String topic, String tag, Object message, String messageType, String messageKey) {
//        try {
//            Message mqMessage = buildMessage(topic, tag, message, messageType, messageKey, null, null);
//            SendReceipt receipt = producer.send(mqMessage);
//            log.info("消息发送成功，消息ID: {}", receipt.getMessageId());
//            return receipt.getMessageId().toString();
//        } catch (ClientException | JsonProcessingException e) {
//            log.error("消息发送失败", e);
//            throw new RuntimeException("消息发送失败", e);
//        }
//    }
//
//    @Override
//    public CompletableFuture<String> asyncSend(String topic, String tag, Object message, String messageType, String messageKey) {
//        try {
//            Message mqMessage = buildMessage(topic, tag, message, messageType, messageKey, null, null);
//            CompletableFuture<SendReceipt> future = producer.sendAsync(mqMessage);
//
//            return future.thenApply(receipt -> {
//                log.info("消息异步发送成功，消息ID: {}", receipt.getMessageId());
//                return receipt.getMessageId().toString();
//            }).exceptionally(throwable -> {
//                log.error("消息异步发送失败", throwable);
//                throw new RuntimeException("消息异步发送失败", throwable);
//            });
//        } catch (JsonProcessingException e) {
//            log.error("消息构建失败", e);
//            CompletableFuture<String> future = new CompletableFuture<>();
//            future.completeExceptionally(e);
//            return future;
//        }
//    }
//
//    @Override
//    public String delaySend(String topic, String tag, Object message, String messageKey, Duration delayTime) {
//        try {
//            Message mqMessage = buildMessage(topic, tag, message, MQConstant.MESSAGE_TYPE_DELAY, messageKey, delayTime, null);
//            SendReceipt receipt = producer.send(mqMessage);
//            log.info("延迟消息发送成功，消息ID: {}", receipt.getMessageId());
//            return receipt.getMessageId().toString();
//        } catch (ClientException | JsonProcessingException e) {
//            log.error("延迟消息发送失败", e);
//            throw new RuntimeException("延迟消息发送失败", e);
//        }
//    }
//
//    @Override
//    public String fifoSend(String topic, String tag, Object message, String messageGroup, String messageKey) {
//        try {
//            Message mqMessage = buildMessage(topic, tag, message, MQConstant.MESSAGE_TYPE_FIFO, messageKey, null, messageGroup);
//            SendReceipt receipt = producer.send(mqMessage);
//            log.info("FIFO消息发送成功，消息ID: {}", receipt.getMessageId());
//            return receipt.getMessageId().toString();
//        } catch (ClientException | JsonProcessingException e) {
//            log.error("FIFO消息发送失败", e);
//            throw new RuntimeException("FIFO消息发送失败", e);
//        }
//    }
//
//    /**
//     * 构建消息对象
//     *
//     * @param topic 主题
//     * @param tag 标签
//     * @param message 消息内容
//     * @param messageType 消息类型
//     * @param messageKey 消息Key
//     * @param delayTime 延迟时间
//     * @param messageGroup 消息组
//     * @return 消息对象
//     */
//    private Message buildMessage(String topic, String tag, Object message, String messageType, String messageKey,
//                                Duration delayTime, String messageGroup) throws JsonProcessingException {
//        // 将消息对象转换为JSON字符串
//        String messageBody = objectMapper.writeValueAsString(message);
//
//        // 构建消息
//        MessageBuilder builder = new MessageBuilderImpl()
//                .setTopic(topic)
//                .setTag(tag)
//                .setBody(messageBody.getBytes(StandardCharsets.UTF_8));
//
//        // 添加消息属性
//        builder.addProperty("messageType", messageType);
//
//        // 添加可选属性
//        Optional.ofNullable(messageKey).ifPresent(builder::setKeys);
//        Optional.ofNullable(delayTime).ifPresent(delay -> builder.setDeliveryTimestamp(System.currentTimeMillis() + delay.toMillis()));
//        Optional.ofNullable(messageGroup).ifPresent(builder::setMessageGroup);
//
//        return builder.build();
//    }
//}
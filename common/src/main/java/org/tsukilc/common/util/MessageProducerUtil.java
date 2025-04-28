//package org.tsukilc.common.util;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.tsukilc.common.constant.MQConstant;
//import org.tsukilc.common.service.MessageService;
//
//import java.time.Duration;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//
///**
// * 消息生产者工具类
// * 提供便捷的消息发送方法
// */
//@Slf4j
//@Component
//public class MessageProducerUtil {
//
//    private final MessageService messageService;
//
//    @Autowired
//    public MessageProducerUtil(MessageService messageService) {
//        this.messageService = messageService;
//    }
//
//    /**
//     * 发送普通消息
//     *
//     * @param topic   主题
//     * @param tag     标签
//     * @param message 消息
//     * @return 消息ID
//     */
//    public String sendMessage(String topic, String tag, Object message) {
//        return messageService.syncSend(topic, tag, message, MQConstant.MESSAGE_TYPE_NORMAL, generateMessageKey());
//    }
//
//    /**
//     * 异步发送普通消息
//     *
//     * @param topic   主题
//     * @param tag     标签
//     * @param message 消息
//     * @return 消息ID的CompletableFuture
//     */
//    public CompletableFuture<String> sendMessageAsync(String topic, String tag, Object message) {
//        return messageService.asyncSend(topic, tag, message, MQConstant.MESSAGE_TYPE_NORMAL, generateMessageKey());
//    }
//
//    /**
//     * 发送延迟消息
//     *
//     * @param topic     主题
//     * @param tag       标签
//     * @param message   消息
//     * @param delayTime 延迟时间
//     * @return 消息ID
//     */
//    public String sendDelayMessage(String topic, String tag, Object message, Duration delayTime) {
//        return messageService.delaySend(topic, tag, message, generateMessageKey(), delayTime);
//    }
//
//    /**
//     * 发送顺序消息
//     *
//     * @param topic        主题
//     * @param tag          标签
//     * @param message      消息
//     * @param messageGroup 消息组，保证同一组内消息顺序
//     * @return 消息ID
//     */
//    public String sendOrderedMessage(String topic, String tag, Object message, String messageGroup) {
//        return messageService.fifoSend(topic, tag, message, messageGroup, generateMessageKey());
//    }
//
//    /**
//     * 生成唯一的消息Key
//     *
//     * @return 消息Key
//     */
//    private String generateMessageKey() {
//        return UUID.randomUUID().toString().replace("-", "");
//    }
//}
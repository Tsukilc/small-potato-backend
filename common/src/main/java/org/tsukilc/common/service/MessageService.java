package org.tsukilc.common.service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * 消息服务接口
 * 平台化设计：提供统一的消息发送接口，屏蔽底层实现细节
 */
public interface MessageService {

    /**
     * 同步发送消息
     *
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @param messageType 消息类型(normal/fifo/delay/transaction)
     * @param messageKey 消息Key，用于消息查询和幂等消费(可选)
     * @return 消息ID
     */
    String syncSend(String topic, String tag, Object message, String messageType, String messageKey);

    /**
     * 异步发送消息
     *
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @param messageType 消息类型(normal/fifo/delay/transaction)
     * @param messageKey 消息Key，用于消息查询和幂等消费(可选)
     * @return CompletableFuture包装的消息ID
     */
    CompletableFuture<String> asyncSend(String topic, String tag, Object message, String messageType, String messageKey);

    /**
     * 延迟发送消息
     *
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @param messageKey 消息Key，用于消息查询和幂等消费(可选)
     * @param delayTime 延迟时间
     * @return 消息ID
     */
    String delaySend(String topic, String tag, Object message, String messageKey, Duration delayTime);
    
    /**
     * 发送FIFO消息(顺序消息)
     *
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @param messageGroup 消息组，保证同一组内消息顺序
     * @param messageKey 消息Key，用于消息查询和幂等消费(可选)
     * @return 消息ID
     */
    String fifoSend(String topic, String tag, Object message, String messageGroup, String messageKey);
} 
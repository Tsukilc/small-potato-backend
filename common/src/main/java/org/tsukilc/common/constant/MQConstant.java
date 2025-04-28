package org.tsukilc.common.constant;

/**
 * 消息队列常量
 */
public class MQConstant {
    
    /**
     * NameServer地址
     */
    public static final String NAME_SERVER_ADDRESS = "rmq:10.21.32.14:8081";
    
    /**
     * 消费者组ID
     */
    public static final String CONSUMER_GROUP = "like-consumer-group";
    
    /**
     * 点赞相关Topic
     */
    public static final String LIKE_TOPIC = "like-topic";
    
    /**
     * 点赞Tag
     */
    public static final String LIKE_TAG = "like";
    
    /**
     * 取消点赞Tag
     */
    public static final String UNLIKE_TAG = "unlike";
    
    /**
     * 消息类型 - 普通消息
     */
    public static final String MESSAGE_TYPE_NORMAL = "normal";
    
    /**
     * 消息类型 - FIFO消息
     */
    public static final String MESSAGE_TYPE_FIFO = "fifo";
    
    /**
     * 消息类型 - 延迟消息
     */
    public static final String MESSAGE_TYPE_DELAY = "delay";
    
    /**
     * 消息类型 - 事务消息
     */
    public static final String MESSAGE_TYPE_TRANSACTION = "transaction";
} 
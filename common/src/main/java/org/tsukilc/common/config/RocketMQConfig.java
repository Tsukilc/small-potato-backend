package org.tsukilc.common.config;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tsukilc.common.constant.MQConstant;

import java.time.Duration;

/**
 * RocketMQ配置类
 * 平台化设计：提供标准化的消息队列交互组件，支持异步消息处理
 */
@Configuration
public class RocketMQConfig {

    /**
     * RocketMQ 客户端服务提供者，用于创建Producer和Consumer
     */
    @Bean
    public ClientServiceProvider clientServiceProvider() {
        return ClientServiceProvider.loadService();
    }

    /**
     * RocketMQ 客户端配置，包含连接信息等
     */
    @Bean
    public ClientConfiguration clientConfiguration() {
        return ClientConfigurationBuilder.newBuilder()
                .setEndpoints(MQConstant.NAME_SERVER_ADDRESS)
                // 设置请求超时时间为3秒
                .setRequestTimeout(Duration.ofSeconds(3))
                // 设置SSL是否启用
                .enableSsl(false)
                .build();
    }

    /**
     * RocketMQ 生产者
     */
    @Bean
    public Producer producer(ClientServiceProvider provider, ClientConfiguration configuration) throws ClientException {
        ProducerBuilder producerBuilder = provider.newProducerBuilder()
                .setClientConfiguration(configuration)
                // 设置生产者主题
                .setTopics(MQConstant.LIKE_TOPIC)
                // 设置发送超时时间为3秒
                .setSendTimeout(Duration.ofSeconds(3))
                // 设置最大重试次数
                .setMaxAttempts(3);
        
        return producerBuilder.build();
    }
} 
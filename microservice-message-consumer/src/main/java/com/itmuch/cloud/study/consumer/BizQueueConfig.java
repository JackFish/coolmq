package com.itmuch.cloud.study.consumer;

import com.coolmq.amqp.util.MQConstants;
import com.itmuch.cloud.study.consumer.listener.BizMessageListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BizQueueConfig {

    /**
     * 1 首先声明要使用哪个交换机
     */
    @Bean
    private DirectExchange businessExchange() {
        return new DirectExchange(MQConstants.BUSINESS_EXCHANGE);
    }

    /**
     * 2 queue的名称bizQueue，以及一些参数配置
     */
    @Bean
    private Queue bizQueue() {
        Map<String, Object> arguments = new HashMap<>();
        /**配置的死信队列*/
        arguments.put("x-dead-letter-exchange", MQConstants.DLX_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstants.DLX_ROUTING_KEY);
        /**消息被确认前的最大等待时间，默认为无限大 */
        //arguments.put("x-message-ttl", 60000);
        /**消息队列的最大大长度，默认永不过期*/
        //arguments.put("x-max-length", 300);
        return new Queue(MQConstants.BUSINESS_QUEUE, true, false, false, arguments);
    }

    /**
     * 3 绑定bizQueue到相应的key
     */
    @Bean
    public Binding bizBinding() {
        return BindingBuilder.bind(bizQueue()).to(businessExchange())
                .with(MQConstants.BUSINESS_KEY);
    }

    /**
     * 4 最后声明一个listener，用来监听
     */
    @Bean
    public SimpleMessageListenerContainer bizListenerContainer(ConnectionFactory connectionFactory,
                                                               BizMessageListener bizMessageListener) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(bizQueue());
        container.setExposeListenerChannel(true);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(bizMessageListener);
        /** 设置消费者能处理未应答消息的最大个数 */
        container.setPrefetchCount(10);
        return container;
    }

    @Bean
    public Queue helloQueue() {
        return new Queue("hello");
    }


}

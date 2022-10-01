package com.macro.mall.demo.mq.rabbitmq;

import constant.RabbitMqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @title: TopicRabbitConfig
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/27 0027 6:55
 * @Version 1.0
 */

@Configuration
public class TopicRabbitConfig {
    @Bean
    public Queue firstQueue() {
        return new Queue(RabbitMqConst.MALL_TEST_TOPIC_MAN);
    }

    @Bean
    public Queue secondQueue() {
        return new Queue(RabbitMqConst.MALL_TEST_TOPIC_WOMAN);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(RabbitMqConst.MALL_TEST_TOPIC_EXCHANGE_1);
    }


    /**
     * 将firstQueue和topicExchange绑定,而且绑定的键值为topic.man
     * 这样只要是消息携带的路由键是topic.man,才会分发到该队列
     *
     * @return
     */
    @Bean
    Binding bindingExchangeMessage() {
        return BindingBuilder.bind(firstQueue()).to(exchange()).with(RabbitMqConst.MALL_TEST_TOPIC_MAN);
    }

    /**
     * 将secondQueue和topicExchange绑定,而且绑定的键值为用上通配路由键规则topic.#
     * 这样只要是消息携带的路由键是以topic.开头,都会分发到该队列
     *
     * @return
     */
    @Bean
    Binding bindingExchangeMessage2() {
        return BindingBuilder.bind(secondQueue()).to(exchange()).with(RabbitMqConst.MALL_TEST_TOPIC_PREFIX);
    }
}

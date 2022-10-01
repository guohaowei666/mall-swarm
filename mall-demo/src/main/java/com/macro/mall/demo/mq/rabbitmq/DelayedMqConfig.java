package com.macro.mall.demo.mq.rabbitmq;

import constant.RabbitMqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @title: 基于延迟插件实现延迟队列
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/29 0029 21:24
 * @Version 1.0
 * <p>
 * rabbitmq_delayed_message_exchange-3.9.0.ez
 */
@Configuration
public class DelayedMqConfig {
    public static final String DIRECT = "direct";

    @Bean
    public Queue delayQueue1() {
        // 第一个参数是创建的queue的名字，第二个参数是是否支持持久化
        return new Queue(RabbitMqConst.MALL_TEST_DELAY_QUEUE_1, true);
    }

    @Bean
    public CustomExchange delayExchange1() {
        Map<String, Object> args = new HashMap<>(1);
        args.put(RabbitMqConst.X_DELAYED_TYPE, DIRECT);
        return new CustomExchange(RabbitMqConst.MALL_TEST_DELAY_EXCHANGE_1, RabbitMqConst.X_DELAYED_MESSAGE, true, false, args);
    }

    @Bean
    public Binding delayBinding1() {
        return BindingBuilder.bind(delayQueue1()).to(delayExchange1()).with(RabbitMqConst.MALL_TEST_DELAY_ROUTING_1).noargs();
    }
}

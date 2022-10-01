package com.macro.mall.demo.mq.rabbitmq;

import constant.RabbitMqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @title: DeadLetterMqConfig
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/29 0029 20:41
 * @Version 1.0
 */
@Configuration
public class DeadLetterMqConfig {
    /**
     * 定义交换机
     *
     * @return
     */
    @Bean
    public DirectExchange exchange1() {
        return new DirectExchange(RabbitMqConst.MALL_TEST_DEAD_EXCHANGE_1, true, false, null);
    }
    @Bean
    public DirectExchange exchange2() {
        return new DirectExchange(RabbitMqConst.MALL_TEST_DEAD_EXCHANGE_2, true, false, null);
    }

    /**
     * RabbitMq不支持修改已经存在的队列和交换机参数,需删除原队列再重新创建才可达到修改参数的功能
     * @return
     */
    @Bean
    public Queue queue1() {
        // 设置如果队列一 出现问题，则通过参数转到mall.test.dead.exchange，mall.test.dead.routing.2 上！
        HashMap<String, Object> map = new HashMap<>(3);
        // 参数绑定 此处的key 固定值，不能随意写
        map.put("x-dead-letter-exchange", RabbitMqConst.MALL_TEST_DEAD_EXCHANGE_2);
        map.put("x-dead-letter-routing-key", RabbitMqConst.MALL_TEST_DEAD_ROUTING_2);
        // 不做延迟队列的话不建议设置延迟时间，否则消息积压达到延迟时间会导致所有消息进入死信队列，从而影响正常业务逻辑
        //经过测试，不设置ttl,默认为0，死信队列直接可以收到消息，类似于fanout
        //map.put("x-message-ttl", 30 * 1000);
        // 队列名称，是否持久化，是否独享、排外的【true:只可以在本次连接中访问】，是否自动删除，队列的其他属性参数
        return new Queue(RabbitMqConst.MALL_TEST_DEAD_QUEUE_1, true, false, false, map);
    }

    @Bean
    public Binding binding1() {
        // 将队列一 通过mall.test.dead.routing.1 key 绑定到mall.test.dead.exchange 交换机上
        return BindingBuilder.bind(queue1()).to(exchange1()).with(RabbitMqConst.MALL_TEST_DEAD_ROUTING_1);
    }

    /**
     * 这个队列二就是一个普通队列
     *
     * @return
     */
    @Bean
    public Queue queue2() {
        return new Queue(RabbitMqConst.MALL_TEST_DEAD_QUEUE_2, true, false, false, null);
    }

    /**
     * 设置队列二的绑定规则
     *
     * @return
     */
    @Bean
    public Binding binding2() {
        // 将队列二通过mall.test.dead.routing.2 key 绑定到mall.test.dead.exchange交换机上！
        return BindingBuilder.bind(queue2()).to(exchange2()).with(RabbitMqConst.MALL_TEST_DEAD_ROUTING_2);
    }
}

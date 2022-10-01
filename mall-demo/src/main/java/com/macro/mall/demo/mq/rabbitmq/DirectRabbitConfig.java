package com.macro.mall.demo.mq.rabbitmq;

import Enums.RabbitMqEnum;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @title: DirectRabbitConfig
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/26 0026 21:58
 * @Version 1.0
 */

@Configuration
public class DirectRabbitConfig {
    /**
     * 队列
     *
     * @return
     */
    @Bean
    public Queue TestDirectQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:独有的。默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);

        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue(RabbitMqEnum.MALL_TEST_DIRECT_1.getQueueName(), true);
    }

    /**
     * Direct交换机
     *
     * @return
     */
    @Bean
    DirectExchange TestDirectExchange() {
        return new DirectExchange(RabbitMqEnum.MALL_TEST_DIRECT_1.getExchangeName(), true, false);
    }

    /**
     * 绑定  将队列和交换机绑定, 并设置用于哪个匹配键
     *
     * @return
     */
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(TestDirectQueue()).to(TestDirectExchange()).with(RabbitMqEnum.MALL_TEST_DIRECT_1.getRouteKey());
    }


    /**
     * 没有绑定Queue
     *
     * @return
     */
    @Bean
    DirectExchange lonelyDirectExchange() {
        return new DirectExchange(RabbitMqEnum.MALL_TEST_DIRECT_2.getExchangeName());
    }

}

package com.macro.mall.demo.mq.rabbitmq;

import Enums.RabbitMqEnum;
import conf.service.RabbitService;
import constant.RabbitMqConst;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @title: SendMessageController
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/26 0026 22:26
 * @Version 1.0
 */
@Api(tags = "SendMessageController")
@Tag(name = "SendMessageController", description = "发送消息")
@RestController
public class SendMessageController {
    @Autowired
    private RabbitService rabbitService;

    @ApiOperation(value = "direct模式发送消息")
    @GetMapping("/sendDirectMessage")
    public Boolean sendDirectMessage(@RequestParam String message) {
        return rabbitService.sendMessage(RabbitMqEnum.MALL_TEST_DIRECT_1.getExchangeName(), RabbitMqEnum.MALL_TEST_DIRECT_1.getRouteKey(), message);
    }

    @ApiOperation(value = "topic模式发送消息1")
    @GetMapping("/sendTopicMessage1")
    public Boolean sendTopicMessage1(@RequestParam String message) {
        return rabbitService.sendMessage(RabbitMqConst.MALL_TEST_TOPIC_EXCHANGE_1, RabbitMqConst.MALL_TEST_TOPIC_MAN, message);
    }

    @ApiOperation(value = "topic模式发送消息2")
    @GetMapping("/sendTopicMessage2")
    public Boolean sendTopicMessage2(@RequestParam String message) {
        return rabbitService.sendMessage(RabbitMqConst.MALL_TEST_TOPIC_EXCHANGE_1, RabbitMqConst.MALL_TEST_TOPIC_WOMAN, message);
    }

    /**
     * ④消息推送成功
     * ④这种情况触发的是 ConfirmCallback(confirm)回调函数。
     *
     * @param message
     * @return
     */
    @ApiOperation(value = "fanout模式发送消息")
    @GetMapping("/sendFanoutMessage")
    public Boolean sendFanoutMessage(@RequestParam String message) {
        return rabbitService.sendMessage(RabbitMqConst.MALL_TEST_FANOUT_EXCHANGE, null, message);
    }

    /**
     * ①这种情况触发的是 ConfirmCallback(confirm) 回调函数。
     * ③消息推送到sever，交换机和队列啥都没找到
     * <p>
     * 消息发送失败：channel error; protocol method: #method<channel.close>
     * (reply-code=404, reply-text=NOT_FOUND - no exchange 'non-existent-exchange' in vhost '/mall', class-id=60, method-id=40)
     *
     * @param message
     * @return
     */
    @ApiOperation(value = "①消息推送到server，但是在server里找不到exchange")
    @GetMapping("/testMessageAck1")
    public Boolean testMessageAck1(@RequestParam String message) {
        return rabbitService.sendMessage("non-existent-exchange", RabbitMqConst.MALL_TEST_ROUTE_KEY_1, message);
    }

    /**
     * ②这种情况触发的是 ConfirmCallback(confirm)和ReturnCallback(returnedMessage)两个回调函数。
     *
     * @param message
     * @return
     */
    @ApiOperation(value = "②消息推送到server，找到exchange了，但是没找到queue")
    @GetMapping("/testMessageAck2")
    public Boolean testMessageAck2(@RequestParam String message) {
        return rabbitService.sendMessage(RabbitMqConst.MALL_TEST_DIRECT_EXCHANGE_2, RabbitMqConst.MALL_TEST_ROUTE_KEY_2, message);
    }

    /**
     * 死信队列有个问题，就是每个消息的延迟时间需要一样，如果第一条消息没过期，那么后续消息不会被检查，就会一直堆积。
     * 但是如果都设置成同一时间，那么延迟任务（延迟时间各不相同）非常多的时候就得需要对应增加无数个队列才能满足需求。
     *
     * @param message
     * @return
     */
    @ApiOperation(value = "死信队列测试")
    @GetMapping("/sendDeadLetter")
    public Boolean sendDeadLetter(@RequestParam String message) {
        return rabbitService.sendMessage(RabbitMqConst.MALL_TEST_DEAD_EXCHANGE_1, RabbitMqConst.MALL_TEST_DEAD_ROUTING_1, message);
    }

    /**
     * 延迟队列（插件）当时间过长例如一个月后，计算出来的时间差long转int后变成负数，此时延时队列失效，立即发送消息。
     * <p>
     * spring.rabbitmq.template.mandatory属性的优先级高于spring.rabbitmq.publisher-returns的优先级
     * spring.rabbitmq.template.mandatory属性可能会返回三种值null、false、true,
     * spring.rabbitmq.template.mandatory结果为true、false时会忽略掉spring.rabbitmq.publisher-returns属性的值
     * spring.rabbitmq.template.mandatory结果为null（即不配置）时结果由spring.rabbitmq.publisher-returns确定
     * <p>
     * 延时消息是从磁盘读取消息然后发送（后台任务），发送消息的时候无法保证两点：
     * 1、发送时消息路由的队列还存在
     * 2、发送时原连接仍然支持回调方法
     * 原因：消息写磁盘和从磁盘读取消息发送存在时间差，两个时间点的队列和连接情况可能不同。所以不支持Mandatory设置
     * <p>
     * 所以延迟队列插件设置mandatory=false才可以正常使用，但是此时spring.rabbitmq.publisher-returns参数就会失效，影响queue消息接收失败回调returnCallback功能
     *
     * @param message
     * @return
     */
    @ApiOperation(value = "延迟队列（插件）测试")
    @GetMapping("/sendDelay1")
    public Boolean sendDelay1(@RequestParam String message) {
        //Integer.MAX_VALUE= 2147483647,而30天代表的毫秒数为2592000000.所以直接支持的延迟时间是有限的
        return rabbitService.sendDelayedMessage(RabbitMqConst.MALL_TEST_DELAY_EXCHANGE_1, RabbitMqConst.MALL_TEST_DELAY_ROUTING_1, message, 10000);
    }
}

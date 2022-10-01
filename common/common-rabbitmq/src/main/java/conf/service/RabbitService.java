package conf.service;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import conf.constant.RedisConst;
import model.GmallCorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import util.SeparatorUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RabbitService {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 发送延迟消息(插件专用)
     * <p>
     * 消费者可以正常消费消息但是有下面的报错
     * 应答码: 312
     * 描述：NO_ROUTE
     * <p>
     * 延时消息是从磁盘读取消息然后发送（后台任务），发送消息的时候无法保证两点：
     * <p>
     * 1、发送时消息路由的队列还存在
     * 2、发送时原连接仍然支持回调方法
     * 原因：消息写磁盘和从磁盘读取消息发送存在时间差，两个时间点的队列和连接情况可能不同。所以不支持Mandatory设置
     *
     * @param exchange
     * @param routingKey
     * @param message
     * @param delayTime  单位ms
     * @return
     */
    public boolean sendDelayedMessage(String exchange, String routingKey, Object message, Integer delayTime) {


        //重试机制-封装对象
        //创建实体类封装消息信息
        GmallCorrelationData gmallCorrelationData = new GmallCorrelationData();

        //设置id
        String correlationDataId = UUID.randomUUID().toString().replaceAll(SeparatorUtil.SHORT_TRANSVERSE_LINE, "");
        gmallCorrelationData.setId(correlationDataId);
        //设置消息
        gmallCorrelationData.setMessage(message);
        //设置交换机
        gmallCorrelationData.setExchange(exchange);
        //设置路由
        gmallCorrelationData.setRoutingKey(routingKey);
        //是否延迟
        gmallCorrelationData.setDelay(true);
        //延迟时间
        gmallCorrelationData.setDelayTime(delayTime);

        //存储到redis
        this.redisTemplate.opsForValue().set(RedisConst.RABBITMQ_TEMPORARY_PREFIX + correlationDataId, JSON.toJSONString(gmallCorrelationData));
        //延迟插件需要此值配置成false
        this.rabbitTemplate.setMandatory(false);
        this.rabbitTemplate.convertAndSend(exchange, routingKey, message, messagePostProcessor -> {
            //设置消息存储模式为永久存储(默认)
            //message1.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            //设置延迟时间（单位为ms）
            messagePostProcessor.getMessageProperties().setDelay(delayTime);
            messagePostProcessor.getMessageProperties().setCorrelationId(correlationDataId);
            return messagePostProcessor;
        }, gmallCorrelationData);

        return true;


    }

    /**
     * 发送消息封装
     *
     * @param exchange
     * @param routingKey
     * @param massage
     * @return
     */
    public boolean sendMessage(String exchange, String routingKey, Object massage) {

        //创建实体类封装消息信息
        GmallCorrelationData gmallCorrelationData = new GmallCorrelationData();

        //设置id
        String correlationDataId = UUID.randomUUID().toString().replaceAll(SeparatorUtil.SHORT_TRANSVERSE_LINE, "");
        gmallCorrelationData.setId(correlationDataId);
        //设置消息
        gmallCorrelationData.setMessage(massage);
        //设置交换机
        gmallCorrelationData.setExchange(exchange);
        //设置路由
        gmallCorrelationData.setRoutingKey(routingKey);
        //设置消息发送时间
        gmallCorrelationData.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        //存储到redis
        redisTemplate.opsForValue().set(RedisConst.RABBITMQ_TEMPORARY_PREFIX + correlationDataId, JSON.toJSONString(gmallCorrelationData),
                10, TimeUnit.MINUTES);
        //Mandatory为true时,消息通过交换器无法匹配到队列会返回给生产者 并触发ReturnCallback为false时,匹配不到会直接被丢弃
        rabbitTemplate.setMandatory(true);
        //发送消息
        rabbitTemplate.convertAndSend(exchange, routingKey, massage, messagePostProcessor -> {
            //不建议设置消息的过期时间，因为消息积压达到过期时间便会被删除或者进入死信队列，存在丢消息或者影响正常业务逻辑的情况
            //messagePostProcessor.getMessageProperties().setExpiration(expiration);
            messagePostProcessor.getMessageProperties().setCorrelationId(correlationDataId);
            return messagePostProcessor;
        }, gmallCorrelationData);


        return true;
    }


}

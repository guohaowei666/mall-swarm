package com.macro.mall.demo.mq.rabbitmq;

import cn.hutool.core.date.DatePattern;
import com.rabbitmq.client.Channel;
import conf.constant.RedisConst;
import constant.RabbitMqConst;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import util.CommonUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @title: Receiver
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/26 0026 22:54
 * @Version 1.0
 * <p>
 * 消费者收到消息后，手动调用basic.ack/basic.nack/basic.reject后，RabbitMQ收到这些消息后，才认为本次投递成功。
 * basic.ack用于肯定确认
 * basic.nack用于否定确认（注意：这是AMQP 0-9-1的RabbitMQ扩展）
 * basic.reject用于否定确认，但与basic.nack相比有一个限制:一次只能拒绝单条消息
 * <p>
 * <p>
 * channel.basicReject(deliveryTag, true);  拒绝消费当前消息，如果第二参数传入true，就是将数据重新丢回队列里，那么下次还会消费这消息。设置false，就是告诉服务器，我已经知道这条消息数据了，因为一些原因拒绝它，而且服务器也把这个消息丢掉就行。 下次不想再消费这条消息了。
 * 使用拒绝后重新入列这个确认模式要谨慎，因为一般都是出现异常的时候，catch异常再拒绝入列，选择是否重入列。
 * 但是如果使用不当会导致一些每次都被你重入列的消息一直消费-入列-消费-入列这样循环，会导致消息积压。
 * <p>
 * channel.basicNack(deliveryTag, false, true);第一个参数依然是当前消息到的数据的唯一id;第二个参数是指是否针对多条消息；如果是true，也就是说一次性针对当前通道的消息的tagID小于当前这条消息的，都拒绝确认。第三个参数是指是否重新入列，也就是指不确认的消息是否重新丢回到队列里面去。
 * 同样使用不确认后重新入列这个确认模式要谨慎，因为这里也可能因为考虑不周出现消息一直被重新丢回去的情况，导致积压。
 * <p>
 * 如果想自定义配置类更细粒度的配置消费者参数，查看我收藏的文档。一般的业务逻辑用下面注解的形式处理够用了
 */
@Slf4j
@Component
public class Receiver {
    @Autowired
    private RedisTemplate redisTemplate;

    private Integer retryCount = 0;

    /**
     * 如何设置了exclusive=true，即消费者独占queue，则concurrency必须=1
     *
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_QUEUE_1, concurrency = "1", exclusive = false)
    //@RabbitHandler
    public void directProcess1(Message message, Channel channel) {
        System.out.println("DirectReceiver1消费者收到消息  : " + new String(message.getBody()));
        // false 确认一个消息，true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_QUEUE_1)
    //@RabbitHandler
    public void directProcess2(Message message, Channel channel) {
        System.out.println("DirectReceiver2消费者收到消息  : " + new String(message.getBody()));
        // false 确认一个消息，true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_TOPIC_MAN)
    //@RabbitHandler
    public void topicProcess1(Message message, Channel channel) {
        System.out.println("TopicManReceiver消费者收到消息  : " + new String(message.getBody()));
        // false 确认一个消息，true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_TOPIC_WOMAN)
    //@RabbitHandler
    public void topicProcess2(Message message, Channel channel) {
        System.out.println("TopicWomanReceiver消费者收到消息  : " + new String(message.getBody()));
        // false 确认一个消息，true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_FANOUT_A)
    //@RabbitHandler
    public void fanoutProcessA(Message message, Channel channel) {
        System.out.println("fanoutReceiverA消费者收到消息  : " + new String(message.getBody()));
        // false 确认一个消息，true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_FANOUT_B)
    //@RabbitHandler
    public void fanoutProcessB(Message message, Channel channel) {
        System.out.println("fanoutReceiverB消费者收到消息  : " + new String(message.getBody()));
        // false 确认一个消息，true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_FANOUT_C)
    //@RabbitHandler
    public void fanoutProcessC(Message message, Channel channel) {
        System.out.println("fanoutReceiverC消费者收到消息  : " + new String(message.getBody()));
        // false 确认一个消息，true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_DEAD_QUEUE_1)
    //@RabbitHandler
    public void getDeadLetter1(Message message, Channel channel) {
        System.out.println("deadLetter1消费者收到消息  : " + new String(message.getBody()));
        // false拒绝进入队列，直接进入死信队列
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        //true重新进入队列，就会不停的重发
        //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_DEAD_QUEUE_2)
    //@RabbitHandler
    public void getDeadLetter2(Message message, Channel channel) {
        System.out.println("deadLetter2消费者收到消息  : " + new String(message.getBody()));
        // false 确认一个消息，true 批量确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 幂等性的一种解决方案
     * setnx--(SET if Not eXists） 命令在指定的 key 不存在时，为 key 设置指定的值
     * 设置成功，返回 1 。 设置失败，返回 0 。
     *
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_DELAY_QUEUE_1)
    public void getUniqueMsg(Message message, Channel channel) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        String uniqueMsgKey = RedisConst.RABBITMQ_UNIQUE_PREFIX + correlationId;
        Boolean result = this.redisTemplate.opsForValue().setIfAbsent(uniqueMsgKey, CommonUtil.FLAG_NUM_FAIL, 10, TimeUnit.MINUTES);
        //  result = true : 说明执行成功，redis 里面没有这个key ，第一次创建， 第一次消费。
        //  result = false : 说明执行失败，redis 里面有这个key
        //  不能： 那么就表示这个消息只能被消费一次！  那么第一次消费成功或失败，我们确定不了！  --- 只能被消费一次！
        //  能： 保证消息被消费成功    第二次消费，可以进来，但是要判断上一个消费者，是否将消息消费了。如果消费了，则直接返回，如果没有消费成功，我消费。
        //  在设置key 的时候给了一个默认值 0 ，如果消费成功，则将key的值 改为1
        if (!result) {
            //  获取缓存key对应的数据
            Integer status = (Integer) this.redisTemplate.opsForValue().get(uniqueMsgKey);
            if (CommonUtil.FLAG_NUM_FAIL.equals(status)) {
                //  说明第一个消费者没有消费成功，所以消费并确认
                System.out.println("getUniqueMsg消费者消费消息成功: " + new String(message.getBody()));
                //  修改redis 中的数据
                this.redisTemplate.opsForValue().set(uniqueMsgKey, CommonUtil.FLAG_NUM_SUCCESS);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        } else {
            System.out.println("getUniqueMsg消费者第一次消费消息成功: " + new String(message.getBody()));
            this.redisTemplate.opsForValue().set(uniqueMsgKey, CommonUtil.FLAG_NUM_SUCCESS);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        log.info("接收时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        log.info("接收的消息correlationId：" + correlationId);
    }

    /**
     * 抛出异常，模拟消费消息失败
     *
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = RabbitMqConst.MALL_TEST_DELAY_QUEUE_1)
    public void getUniqueMsgFail(Message message, Channel channel){
        String correlationId = message.getMessageProperties().getCorrelationId();
        String uniqueMsgKey = RedisConst.RABBITMQ_UNIQUE_PREFIX + correlationId;
        Boolean result = this.redisTemplate.opsForValue().setIfAbsent(uniqueMsgKey, CommonUtil.FLAG_NUM_FAIL, 10, TimeUnit.MINUTES);
        //  result = true : 说明执行成功，redis 里面没有这个key ，第一次创建， 第一次消费。
        //  result = false : 说明执行失败，redis 里面有这个key
        //  不能： 那么就表示这个消息只能被消费一次！  那么第一次消费成功或失败，我们确定不了！  --- 只能被消费一次！
        //  能： 保证消息被消费成功    第二次消费，可以进来，但是要判断上一个消费者，是否将消息消费了。如果消费了，则直接返回，如果没有消费成功，我消费。
        //  在设置key 的时候给了一个默认值 0 ，如果消费成功，则将key的值 改为1
        try {
            if (!result) {
                //  获取缓存key对应的数据
                Integer status = (Integer) this.redisTemplate.opsForValue().get(uniqueMsgKey);
                if (CommonUtil.FLAG_NUM_FAIL.equals(status)) {
                    int i = 2 / 0;
                    System.out.println("getUniqueMsg消费者消费消息成功: " + new String(message.getBody()));
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    //  修改redis 中的数据
                    this.redisTemplate.opsForValue().set(uniqueMsgKey, CommonUtil.FLAG_NUM_SUCCESS);
                }
            } else {
                int i = 2 / 0;
                System.out.println("getUniqueMsg消费者第一次消费消息成功: " + new String(message.getBody()));
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                this.redisTemplate.opsForValue().set(uniqueMsgKey, CommonUtil.FLAG_NUM_SUCCESS);
            }
            log.info("接收时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            log.info("接收的消息correlationId：" + correlationId);
        } catch (Exception e) {
            retryCount++;
            throw e;
        } finally {
            if (retryCount >= 3) {
                //重试次数到了最大值，则将此消息放入到死信队列
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                retryCount = 0;
            }
        }
    }
}

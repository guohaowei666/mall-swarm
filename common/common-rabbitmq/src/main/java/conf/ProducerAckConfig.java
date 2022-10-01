package conf;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import model.GmallCorrelationData;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * 1. 如果消息没有到exchange,则confirm回调,ack=false
 * 2. 如果消息到达exchange,则confirm回调,ack=true
 * 3. exchange到queue成功,则不回调return
 * 4. exchange到queue失败,则回调return
 */
@Component
@Slf4j
public class ProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final Integer MAX_RETRY_COUNT = 3;

    /**
     * ConfirmCallback  只确认消息是否正确到达 Exchange 中
     * ReturnCallback   消息没有正确到达队列时触发回调，如果正确到达队列不执行
     * <p>
     * 饰一个非静态的void（）方法,在服务器加载Servlet的时候运行，并且只会被服务器执行一次在构造函数之后执行，init（）方法之前执行。
     */
    @PostConstruct
    public void init() {
        this.rabbitTemplate.setConfirmCallback(this);
        this.rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 确认消息是否到达交换机
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息发送到exchange成功：" + JSON.toJSONString(correlationData));
        } else {
            log.info("消息发送到exchange失败：" + cause + " 数据：" + JSON.toJSONString(correlationData));

            this.retryMessage(correlationData);
        }
    }

    /**
     * 确实消息是否正确到达队列
     * 执行时机：
     * 到达不执行
     * 没到执行
     *
     * @param returnedMessage
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        Message message = returnedMessage.getMessage();
        log.error("消息主体:{},应答码:{},描述:{}消息使用的交换器exchange:{},消息使用的路由键routing:{}", new String(message.getBody()),
                returnedMessage.getReplyCode(), returnedMessage.getReplyText(), returnedMessage.getExchange(), returnedMessage.getRoutingKey());
        //从redis中获取数据
        String correlationDataId = (String) message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        if (StringUtils.hasText(correlationDataId)) {
            String strJson = (String) redisTemplate.opsForValue().get(correlationDataId);
            if (StringUtils.hasText(strJson)) {
                GmallCorrelationData gmallCorrelationData = JSON.parseObject(strJson, GmallCorrelationData.class);
                //调用重试方法
                this.retryMessage(gmallCorrelationData);
            }
        }
    }

    /**
     * 借助redis来实现重发机制
     *
     * @param correlationData
     */
    private void retryMessage(CorrelationData correlationData) {

        //数据类型转换  统一转换为子类处理
        GmallCorrelationData gmallCorrelationData = (GmallCorrelationData) correlationData;
        //获取重试次数 初始值 0
        int retryCount = gmallCorrelationData.getRetryCount();
        //判断
        if (retryCount >= MAX_RETRY_COUNT) {
            log.error("重发【{}】次后仍然失败，请排查原因：{}", retryCount, JSON.toJSONString(correlationData));
            //TODO 将消息加入到死信队列里或者根据日志信息，人工处理
        } else {
            retryCount++;
            gmallCorrelationData.setRetryCount(retryCount);
            //更新redis
            redisTemplate.opsForValue().set(gmallCorrelationData.getId(), JSON.toJSONString(gmallCorrelationData));
            System.out.println("重试次数：\t" + retryCount);
            //判断是否延迟
            if (gmallCorrelationData.isDelay()) {
                this.rabbitTemplate.convertAndSend(gmallCorrelationData.getExchange(), gmallCorrelationData.getRoutingKey(),
                        gmallCorrelationData.getMessage(), message -> {
                            message.getMessageProperties().setDelay(gmallCorrelationData.getDelayTime() * 1000);
                            return message;
                        }, gmallCorrelationData);
            } else {
                //重新发送
                this.rabbitTemplate.convertAndSend(gmallCorrelationData.getExchange(), gmallCorrelationData.getRoutingKey(),
                        gmallCorrelationData.getMessage(), gmallCorrelationData);
            }
        }
    }

}

package Enums;

import constant.RabbitMqConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息队列枚举配置
 *
 * @author 郭浩伟
 * @date 2022-9-26 22:21:24
 */
@Getter
@AllArgsConstructor
public enum RabbitMqEnum {
    /**
     * 消息通知队列
     */
    MALL_TEST_DIRECT_1(RabbitMqConst.MALL_TEST_DIRECT_EXCHANGE_1, RabbitMqConst.MALL_TEST_QUEUE_1, RabbitMqConst.MALL_TEST_ROUTE_KEY_1),
    MALL_TEST_DIRECT_2(RabbitMqConst.MALL_TEST_DIRECT_EXCHANGE_2, RabbitMqConst.MALL_TEST_QUEUE_2, RabbitMqConst.MALL_TEST_ROUTE_KEY_2),
    ;

    /**
     * 交换名称
     */
    private String exchangeName;
    /**
     * 队列名称
     */
    private String queueName;
    /**
     * 路由键
     */
    private String routeKey;

}

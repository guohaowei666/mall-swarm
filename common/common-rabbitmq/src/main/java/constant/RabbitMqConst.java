package constant;

/**
 * @title: RabbitMqConst
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/25 0025 18:14
 * @Version 1.0
 */
public interface RabbitMqConst {
    /**
     * 死信固定参数，不可修改
     */
    String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    String X_MESSAGE_TTL = "x-message-ttl";
    /**
     * 延迟（插件）队列固定参数，不可修改
     */
    String X_DELAYED_TYPE = "x-delayed-type";
    String X_DELAYED_MESSAGE = "x-delayed-message";

    /**
     * demo测试用-direct
     */
    String MALL_TEST_DIRECT_EXCHANGE_1 = "mall.test.direct.exchange.1";
    String MALL_TEST_QUEUE_1 = "mall.test.queue.1";
    String MALL_TEST_ROUTE_KEY_1 = "mall.test.routeKey.1";

    String MALL_TEST_DIRECT_EXCHANGE_2 = "mall.test.direct.exchange.2";
    String MALL_TEST_QUEUE_2 = "mall.test.queue.2";
    String MALL_TEST_ROUTE_KEY_2 = "mall.test.routeKey.2";
    /**
     * demo测试用-topic
     * 为了开发便利。queue的name和routeKey的name设为一样
     * # 匹配零个或者多个
     * * 匹配一个
     */
    String MALL_TEST_TOPIC_EXCHANGE_1 = "mall.test.topic.exchange.1";
    String MALL_TEST_TOPIC_MAN = "mall.test.topic.man";
    String MALL_TEST_TOPIC_WOMAN = "mall.test.topic.woman";
    String MALL_TEST_TOPIC_PREFIX = "mall.test.topic.#";
    /**
     * demo测试用-fanout
     */
    String MALL_TEST_FANOUT_A = "mall.test.fanout.a";
    String MALL_TEST_FANOUT_B = "mall.test.fanout.b";
    String MALL_TEST_FANOUT_C = "mall.test.fanout.c";
    String MALL_TEST_FANOUT_EXCHANGE = "mall.test.fanout.exchange";
    /**
     * demo测试用-死信队列
     */
    String MALL_TEST_DEAD_EXCHANGE_1 = "mall.test.dead.exchange_1";
    String MALL_TEST_DEAD_EXCHANGE_2 = "mall.test.dead.exchange_2";
    String MALL_TEST_DEAD_ROUTING_1 = "mall.test.dead.routing.1";
    String MALL_TEST_DEAD_ROUTING_2 = "mall.test.dead.routing.2";
    String MALL_TEST_DEAD_QUEUE_1 = "mall.test.dead.queue.1";
    String MALL_TEST_DEAD_QUEUE_2 = "mall.test.dead.queue.2";
    /**
     * demo测试用-延迟队列（插件）
     */
    String MALL_TEST_DELAY_EXCHANGE_1 = "mall.test.delay.exchange_1";
    String MALL_TEST_DELAY_ROUTING_1 = "mall.test.delay.exchange_1";
    String MALL_TEST_DELAY_QUEUE_1 = "mall.test.delay.queue_1";
}

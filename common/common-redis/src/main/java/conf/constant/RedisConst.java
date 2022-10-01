package conf.constant;

/**
 * @title: RedisConst
 * @Author 郭浩伟 qq:912161367
 * @Date: 2022/9/24 0024 17:27
 * @Version 1.0
 */
public interface RedisConst {
    /**
     * Redis缓存权限规则key
     */
    String RESOURCE_ROLES_MAP_KEY = "auth:resourceRolesMap";
    /**
     * mall项目admin模块用户信息前缀
     */
    String MALL_UMS_ADMIN_PREFIX = "mall:ums:admin:";
    /**
     * rabbitmq发送消息的临时存储，供失败后重发使用
     */
    String RABBITMQ_TEMPORARY_PREFIX = "rabbitmq:temporary:";
    /**
     * rabbitmq发送的消息通过redis的setnx保证幂等性
     */
    String RABBITMQ_UNIQUE_PREFIX = "rabbitmq:unique:";
}

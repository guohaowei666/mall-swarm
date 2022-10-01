package com.macro.mall.service.impl;

import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.UmsAdminCacheService;
import conf.constant.RedisConst;
import conf.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * UmsAdminCacheService实现类
 * Created by macro on 2020/3/13.
 */
@Service
public class UmsAdminCacheServiceImpl implements UmsAdminCacheService {
    @Autowired
    private RedisService redisService;

    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;


    @Override
    public void delAdmin(Long adminId) {
        String key = RedisConst.MALL_UMS_ADMIN_PREFIX + adminId;
        redisService.del(key);
    }

    @Override
    public UmsAdmin getAdmin(Long adminId) {
        String key = RedisConst.MALL_UMS_ADMIN_PREFIX + adminId;
        return (UmsAdmin) redisService.get(key);
    }

    @Override
    public void setAdmin(UmsAdmin admin) {
        String key = RedisConst.MALL_UMS_ADMIN_PREFIX + admin.getId();
        redisService.set(key, admin, REDIS_EXPIRE);
    }
}

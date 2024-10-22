package com.qyling.self_bi.manager;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 限流中间件
 */
@Service
public class RateLimiterManager {
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 初始化限流器
     *
     * @param key  限流器的唯一标识
     * @param rate 每秒允许的请求数
     * @return RRateLimiter 限流器对象
     */
    private RRateLimiter createRateLimiter(String key, long rate) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 设置限流规则：每秒允许 rate 次请求
        rateLimiter.trySetRate(RateType.OVERALL, rate, 1, RateIntervalUnit.SECONDS);
        return rateLimiter;
    }

    /**
     * 尝试获取许可
     *
     * @param key  限流器的唯一标识
     * @param rate 每秒允许的请求数
     * @return 是否获取到许可 boolean
     */
    public boolean tryAcquire(String key, long rate) {
        RRateLimiter rateLimiter = createRateLimiter(key, rate);
        // 尝试获取许可
        return rateLimiter.tryAcquire();
    }
}

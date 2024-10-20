package com.qyling.self_bi.manager;

import com.qyling.self_bi.common.ErrorCode;
import com.qyling.self_bi.exception.ThrowUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RateLimiterManagerTest {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RateLimiterManager rateLimiterManager;

    @Test
    void redissonQuickStart() {
        Assertions.assertTrue(redissonClient.getKeys().count() != 0);
    }

    @Test
    void tryAcquire() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            boolean remain = rateLimiterManager.tryAcquire("testRateLimit", 3);
            ThrowUtils.throwIf(!remain, ErrorCode.TOO_MANY_REQUESTS);
            System.out.println("exec =======> " + i);
        }
    }
}
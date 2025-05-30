package com.qyling.self_bi;

import com.qyling.self_bi.model.properties.AIServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 主类测试
 */
@SpringBootTest
@Slf4j
class MainApplicationTests {

    @Resource
    private AIServiceProperties aiServiceProperties;
    @Test
    void testInjection() {
        log.info(aiServiceProperties.getModel());
    }

}

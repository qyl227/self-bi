package com.qyling.self_bi;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.ObjectMapper;
import com.qyling.self_bi.model.message.GenChartMessage;
import com.qyling.self_bi.model.properties.AIServiceProperties;
import com.qyling.self_bi.mq.consumer.GenChartByAIConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

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

    @Test
    void testDeserialize() {
        byte[] data = new byte[]{123, 34, 99, 104, 97, 114, 116, 73, 100, 34, 58, 49, 57, 50, 56, 51, 50, 51, 54, 53, 50, 56, 48, 57, 56, 49, 52, 48, 49, 56, 44, 34, 109, 111, 100, 101, 108, 34, 58, 110, 117, 108, 108, 125};
        GenChartMessage genChartMessage = JSONUtil.toBean(new String(data, StandardCharsets.UTF_8), GenChartMessage.class);
        log.info(genChartMessage.toString());

    }

}

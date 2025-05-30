package com.qyling.self_bi.utils;

import com.qyling.self_bi.manager.AIManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class AiManagerTest {

    @Resource
    private AIManager aiManager;
    @Test
    void doChat() {
        log.info(aiManager.doChat(
                "gpt-4o-mini",
                "",
                "https://ai.liaobots.work/v1/chat/completions",
                "分析数据:\n" +
                "日期,销量\n" +
                "1,20\n" +
                "2,40\n" +
                "3,50\n" +
                "4,55\n" +
                "分析目标:帮我分析某商品的销量增长趋势")
        );;
    }

}

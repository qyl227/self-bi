package com.qyling.self_bi.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class AiUtilsTest {

    public static void main(String[] args) {
        log.debug(AIUtils.doChat("分析数据:\n" +
                "日期,销量\n" +
                "1,20\n" +
                "2,40\n" +
                "3,50\n" +
                "4,55\n" +
                "分析目标:帮我分析某商品的销量增长趋势"));;
    }

}

package com.yupi.springbootinit.utils;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.AIConstant;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.chart.ChartAIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

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
                "分析目标:帮我分析某商品的销量增长趋势"));

//        String s = "{\n" +
//                "  \"title\": {\n" +
//                "    \"text\": \"商品销量增长趋势\"\n" +
//                "  },\n" +
//                "  \"tooltip\": {\n" +
//                "    \"trigger\": \"axis\"\n" +
//                "  },\n" +
//                "  \"xAxis\": {\n" +
//                "    \"type\": \"category\",\n" +
//                "    \"data\": [\"1\", \"2\", \"3\", \"4\"],\n" +
//                "    \"name\": \"日期\"\n" +
//                "  },\n" +
//                "  \"yAxis\": {\n" +
//                "    \"type\": \"value\",\n" +
//                "    \"name\": \"销量\"\n" +
//                "  },\n" +
//                "  \"series\": [\n" +
//                "    {\n" +
//                "      \"data\": [20, 40, 50, 55],\n" +
//                "      \"type\": \"line\",\n" +
//                "      \"smooth\": true,\n" +
//                "      \"name\": \"销量\"\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}\n" +
//                "}}}}}\n" +
//                "从提供的数据来看，该商品在四天内的销量呈现出明显的上升趋势。具体而言，第一天的销量为20件，第二天迅速增长至40件，增幅为100%。第三天销量增长放缓，达到50件，而第四天的销量继续攀升至55件，增幅逐渐减小。\n" +
//                "\n" +
//                "整体上，商品的销量在前两天的增幅较大，表现出较强的市场需求，可能与促销活动或营销策略有关。然而，第三天和第四天的销量增速有所放缓，显示出增长趋势的趋稳迹象。这可能意味着市场需求逐渐接近饱和，或者在短期内缺乏进一步的驱动因素。\n" +
//                "\n" +
//                "未来，若想保持销量增长，企业可能需要采取额外的市场激励措施或推出相关联的产品来刺激需求。同时，监测用户购买行为和市场反馈有助于判断这种增长是否具有可持续性，以及是否需要调整销售策略以应对市场变化。\n";
//        String[] strings = s.split("}}}}}");
//        ChartAIResponse chartAIResponse = new ChartAIResponse();
//        ThrowUtils.throwIf(strings.length != 2, ErrorCode.OPERATION_ERROR, "处理AI结果失败");
//        chartAIResponse.setGenChart(strings[0]);
//        chartAIResponse.setGenResult(strings[1]);
//        log.debug(chartAIResponse.toString());
    }

}

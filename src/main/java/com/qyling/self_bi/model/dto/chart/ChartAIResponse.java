package com.qyling.self_bi.model.dto.chart;

import lombok.Data;

@Data
public class ChartAIResponse {
    /**
     * 生成的图表数据
     */
    private String genChart;

    /**
     * 生成的分析结论
     */
    private String genResult;
}

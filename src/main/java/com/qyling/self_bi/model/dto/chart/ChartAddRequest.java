package com.qyling.self_bi.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChartAddRequest implements Serializable {

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * ai模型
     */
    private String aiModel;

    /**
     * 调用ai所需的密钥
     */
    private String secretKey;

    /**
     * ai服务商的url
     */
    private String apiUrl;

    private static final long serialVersionUID = 1L;
}

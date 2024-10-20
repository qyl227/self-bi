package com.qyling.self_bi.model.dto.chart;

import com.qyling.self_bi.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChartQueryRequest extends PageRequest implements Serializable {
    private Long id;
    /**
     * 图表名称
     */
    private String name;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}

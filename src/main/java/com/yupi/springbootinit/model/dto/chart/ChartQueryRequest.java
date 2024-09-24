package com.yupi.springbootinit.model.dto.chart;

import lombok.Data;
import com.yupi.springbootinit.common.PageRequest;
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

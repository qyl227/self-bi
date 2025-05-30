package com.qyling.self_bi.model.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author qyling
 * @date 2025/5/30 12:57
 */
@Data
@Builder
public class GenChartMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long chartId;
    private String model;
    private String secretKey;
    private String apiUrl;
}

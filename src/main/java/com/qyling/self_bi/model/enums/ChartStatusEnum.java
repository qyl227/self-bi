package com.qyling.self_bi.model.enums;

import lombok.Getter;

/**
 * 图表状态枚举
 */
public enum ChartStatusEnum {
    WAIT("等待", "wait"),
    RUNNING("运行中", "running"),
    SUCCEED("成功", "succeed"),
    FAILED("失败", "failed");

    private final String text;
    @Getter
    private final String value;


    ChartStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}

package com.qyling.self_bi.model.enums;


public enum ChartTypeEnum {
    LINE("折线图", "line"),
    BAR("柱状图", "bar"),
    PIE("饼图", "pie"),
    SCATTER("散点图", "scatter"),
    MAP("地图", "map");

    private final String text;
    private final String value;

    ChartTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}

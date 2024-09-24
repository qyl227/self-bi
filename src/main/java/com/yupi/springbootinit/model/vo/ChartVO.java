package com.yupi.springbootinit.model.vo;

import com.yupi.springbootinit.model.entity.Chart;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data
public class ChartVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 生成的图表数据
     */
    private String genChart;

    /**
     * 生成的分析结论
     */
    private String genResult;

    /**
     * wait,running,succeed,failed
     */
    private String status;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param chartVO
     * @return
     */
    public static Chart voToObj(ChartVO chartVO) {
        if (chartVO == null) {
            return null;
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartVO, chart);
        return chart;
    }

    /**
     * 对象转包装类
     *
     * @param chart
     * @return
     */
    public static ChartVO objToVo(Chart chart) {
        if (chart == null) {
            return null;
        }
        ChartVO chartVO = new ChartVO();
        BeanUtils.copyProperties(chart, chartVO);
        return chartVO;
    }
}

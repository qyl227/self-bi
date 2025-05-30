package com.qyling.self_bi.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qyling.self_bi.common.BaseResponse;
import com.qyling.self_bi.model.dto.chart.ChartAIResponse;
import com.qyling.self_bi.model.entity.Chart;
import com.qyling.self_bi.model.vo.ChartVO;
import com.qyling.self_bi.model.dto.chart.ChartAddRequest;
import com.qyling.self_bi.model.dto.chart.ChartQueryRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author 花花
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2024-09-17 12:47:53
*/
public interface ChartService extends IService<Chart> {

    void validChart(Chart chart);

    Wrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);

    Page<ChartVO> getChartVOPage(Page<Chart> chartPage);

    ChartAIResponse analyzeChartByAI(String aiModel, String secretKey, String apiUrl, String csvData);

    BaseResponse<ChartVO> genChartByAI(MultipartFile multipartFile, ChartAddRequest chartAddRequest, HttpServletRequest request);
}

package com.qyling.self_bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qyling.self_bi.common.BaseResponse;
import com.qyling.self_bi.common.ErrorCode;
import com.qyling.self_bi.common.ResultUtils;
import com.qyling.self_bi.constant.CommonConstant;
import com.qyling.self_bi.constant.MQConstant;
import com.qyling.self_bi.exception.BusinessException;
import com.qyling.self_bi.exception.ThrowUtils;
import com.qyling.self_bi.mapper.ChartMapper;
import com.qyling.self_bi.model.dto.chart.ChartAIResponse;
import com.qyling.self_bi.model.entity.Chart;
import com.qyling.self_bi.model.entity.User;
import com.qyling.self_bi.model.enums.ChartStatusEnum;
import com.qyling.self_bi.model.vo.ChartVO;
import com.qyling.self_bi.mq.MessageSupplier;
import com.qyling.self_bi.utils.ExcelUtils;
import com.qyling.self_bi.utils.SqlUtils;
import com.qyling.self_bi.model.dto.chart.ChartAddRequest;
import com.qyling.self_bi.model.dto.chart.ChartQueryRequest;
import com.qyling.self_bi.service.ChartService;
import com.qyling.self_bi.service.UserService;
import com.qyling.self_bi.utils.AIUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
* @author 花花
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-09-17 12:47:53
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{
    @Resource
    private UserService userService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    @Lazy
    private MessageSupplier messageSupplier;


    public void validChart(Chart chart) {
        if (chart == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String goal = chart.getGoal();
        String name = chart.getName();
        if (StringUtils.isBlank(goal) || goal.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分析目标不能为空或过长");
        }
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图表名不能为空或过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {

        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id); // satisfaction
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<ChartVO> getChartVOPage(Page<Chart> chartPage) {
        List<Chart> records = chartPage.getRecords();
        List<ChartVO> chartVOS = records.stream().map(ChartVO::objToVo).collect(Collectors.toList());
        Page<ChartVO> chartVOPage = new Page<>(chartPage.getCurrent(), chartPage.getSize(), chartPage.getTotal());
        chartVOPage.setRecords(chartVOS);
        return chartVOPage;
    }

    @Override
    public ChartAIResponse analyzeChartByAI(String userContent) {
        ThrowUtils.throwIf(StringUtils.isBlank(userContent), ErrorCode.PARAMS_ERROR, "请求参数错误");
        String genResult = AIUtils.doChat(userContent);
        String[] strings = genResult.split("}}}}}");
        ChartAIResponse chartAIResponse = new ChartAIResponse();
        ThrowUtils.throwIf(strings.length != 2, ErrorCode.OPERATION_ERROR, "处理AI结果失败");
        chartAIResponse.setGenChart(strings[0]);
        chartAIResponse.setGenResult(strings[1]);
        return chartAIResponse;
    }

    @Override
    public BaseResponse<ChartVO> genChartByAI(MultipartFile multipartFile, ChartAddRequest chartAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(chartAddRequest), ErrorCode.PARAMS_ERROR);

        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        validChart(chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        chart.setStatus(ChartStatusEnum.RUNNING);
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        chart.setChartData(csvData);
        boolean save = save(chart);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "数据库异常");
        // MQ异步调用AI
        messageSupplier.sendMessage(MQConstant.GEN_CHART_BY_AI_QUEUE_NAME, chart.getId(), chart.getId());
        return ResultUtils.success(ChartVO.objToVo(chart));
    }


}





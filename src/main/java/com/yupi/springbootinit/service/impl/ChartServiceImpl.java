package com.yupi.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.chart.ChartAIResponse;
import com.yupi.springbootinit.model.dto.chart.ChartAddRequest;
import com.yupi.springbootinit.model.dto.chart.ChartQueryRequest;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.ChartVO;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.mapper.ChartMapper;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.AIUtils;
import com.yupi.springbootinit.utils.ExcelUtils;
import com.yupi.springbootinit.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.title;

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


    // TODO
    public void validChart(Chart chart, boolean add) {
        if (chart == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        String title = chart.getTitle();
//        String content = chart.getContent();
//        String tags = chart.getTags();
//        // 创建时，参数不能为空
//        if (add) {
//            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
//        }
//        // 有参数则校验
//        if (StringUtils.isNotBlank(title) && title.length() > 80) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
//        }
//        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
//        }
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
        int current = chartQueryRequest.getCurrent();
        int pageSize = chartQueryRequest.getPageSize();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
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
        String goal = chartAddRequest.getGoal();
        String name = chartAddRequest.getName();
        String chartType = chartAddRequest.getChartType();
        ThrowUtils.throwIf(StringUtils.isAnyBlank(name, chartType, goal), ErrorCode.PARAMS_ERROR);

        // excel数据转为csv格式
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        StringBuilder sb = new StringBuilder();
        sb.append("分析数据:\n" + csvData).append("分析目标:").append(goal + "\n").append("图表类型:");
        if ("undefined".equals(chartType)) sb.append("{{未定义，请根据需求选择合适的图表类型}}");
        else sb.append(chartType);
        // 调用AI
        ChartAIResponse chartAIResponse = analyzeChartByAI(sb.toString());
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        chart.setChartData(csvData);
        BeanUtils.copyProperties(chartAIResponse, chart);
        validChart(chart, true);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean save = save(chart);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(ChartVO.objToVo(chart));
    }


}





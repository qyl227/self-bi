package com.yupi.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.chart.ChartQueryRequest;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.vo.ChartVO;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.mapper.ChartMapper;
import com.yupi.springbootinit.service.UserService;
import com.yupi.springbootinit.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

//    public Page<Chart> searchFromEs(ChartQueryRequest chartQueryRequest) {
//        Long id = chartQueryRequest.getId();
//        Long notId = chartQueryRequest.getNotId();
//        String searchText = chartQueryRequest.getSearchText();
//        String title = chartQueryRequest.getTitle();
//        String content = chartQueryRequest.getContent();
//        List<String> tagList = chartQueryRequest.getTags();
//        List<String> orTagList = chartQueryRequest.getOrTags();
//        Long userId = chartQueryRequest.getUserId();
//        // es 起始页为 0
//        long current = chartQueryRequest.getCurrent() - 1;
//        long pageSize = chartQueryRequest.getPageSize();
//        String sortField = chartQueryRequest.getSortField();
//        String sortOrder = chartQueryRequest.getSortOrder();
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        // 过滤
//        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
//        if (id != null) {
//            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
//        }
//        if (notId != null) {
//            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
//        }
//        if (userId != null) {
//            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
//        }
//        // 必须包含所有标签
//        if (CollUtil.isNotEmpty(tagList)) {
//            for (String tag : tagList) {
//                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
//            }
//        }
//        // 包含任何一个标签即可
//        if (CollUtil.isNotEmpty(orTagList)) {
//            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
//            for (String tag : orTagList) {
//                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tags", tag));
//            }
//            orTagBoolQueryBuilder.minimumShouldMatch(1);
//            boolQueryBuilder.filter(orTagBoolQueryBuilder);
//        }
//        // 按关键词检索
//        if (StringUtils.isNotBlank(searchText)) {
//            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
//            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
//            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
//            boolQueryBuilder.minimumShouldMatch(1);
//        }
//        // 按标题检索
//        if (StringUtils.isNotBlank(title)) {
//            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
//            boolQueryBuilder.minimumShouldMatch(1);
//        }
//        // 按内容检索
//        if (StringUtils.isNotBlank(content)) {
//            boolQueryBuilder.should(QueryBuilders.matchQuery("content", content));
//            boolQueryBuilder.minimumShouldMatch(1);
//        }
//        // 排序
//        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
//        if (StringUtils.isNotBlank(sortField)) {
//            sortBuilder = SortBuilders.fieldSort(sortField);
//            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
//        }
//        // 分页
//        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
//        // 构造查询
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
//                .withPageable(pageRequest).withSorts(sortBuilder).build();
//        SearchHits<ChartEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, ChartEsDTO.class);
//        Page<Chart> page = new Page<>();
//        page.setTotal(searchHits.getTotalHits());
//        List<Chart> resourceList = new ArrayList<>();
//        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
//        if (searchHits.hasSearchHits()) {
//            List<SearchHit<ChartEsDTO>> searchHitList = searchHits.getSearchHits();
//            List<Long> chartIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
//                    .collect(Collectors.toList());
//            List<Chart> chartList = baseMapper.selectBatchIds(chartIdList);
//            if (chartList != null) {
//                Map<Long, List<Chart>> idChartMap = chartList.stream().collect(Collectors.groupingBy(Chart::getId));
//                chartIdList.forEach(chartId -> {
//                    if (idChartMap.containsKey(chartId)) {
//                        resourceList.add(idChartMap.get(chartId).get(0));
//                    } else {
//                        // 从 es 清空 db 已物理删除的数据
//                        String delete = elasticsearchRestTemplate.delete(String.valueOf(chartId), ChartEsDTO.class);
//                        log.info("delete chart {}", delete);
//                    }
//                });
//            }
//        }
//        page.setRecords(resourceList);
//        return page;
//    }
}





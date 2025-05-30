package com.qyling.self_bi.mq.consumer;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.util.StringUtils;
import com.qyling.self_bi.common.ErrorCode;
import com.qyling.self_bi.constant.MQConstant;
import com.qyling.self_bi.exception.BusinessException;
import com.qyling.self_bi.exception.ThrowUtils;
import com.qyling.self_bi.model.dto.chart.ChartAIResponse;
import com.qyling.self_bi.model.entity.Chart;
import com.qyling.self_bi.model.enums.ChartStatusEnum;
import com.qyling.self_bi.model.message.GenChartMessage;
import com.qyling.self_bi.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

@Component
@Slf4j
public class GenChartByAIConsumer {
    @Resource
    private ChartService chartService;

    @RabbitListener(queues = MQConstant.GEN_CHART_BY_AI_QUEUE_NAME, ackMode = "MANUAL")
    public void genChartByAI(Message message, Channel channel) throws IOException {
        Chart chart = null;
        try {
            if (message.getBody() == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息队列消费异常");
            }
//            Long chartId = Long.valueOf(new String(message.getBody()));
            byte[] messageBody = message.getBody();
            // 反序列化消息
            GenChartMessage genChartMessage = JSONUtil.toBean(new String(messageBody, StandardCharsets.UTF_8), GenChartMessage.class);
            if (genChartMessage == null) {
                log.error("消息体反序列化后为空");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "请稍后重试");
            }
            Long chartId = genChartMessage.getChartId();
            chart = chartService.getById(chartId);
            String secretKey = genChartMessage.getSecretKey();
            String model = genChartMessage.getModel();
            String apiUrl = genChartMessage.getApiUrl();
            // excel数据转为csv格式
            String csvData = chart.getChartData();
            StringBuilder sb = new StringBuilder();
            sb.append("分析数据:\n" + csvData).append("分析目标:").append(chart.getGoal() + "\n").append("图表类型:");
            if (StringUtils.isEmpty(chart.getChartType())) sb.append("{{未定义，请根据需求选择合适的图表类型}}");
            else sb.append(chart.getChartType());
            // 调用AI
            ChartAIResponse chartAIResponse = chartService.analyzeChartByAI(model, secretKey, apiUrl, sb.toString());
            chart.setChartData(csvData);
            BeanUtils.copyProperties(chartAIResponse, chart);
            chart.setStatus(ChartStatusEnum.SUCCEED);
            chart.setExecMessage("成功");
            boolean save = chartService.updateById(chart);
            ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "数据库异常");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            if (chart != null) {
                chart.setStatus(ChartStatusEnum.FAILED);
                log.error(Arrays.toString(e.getStackTrace()));
                chart.setExecMessage(e.getMessage());
                chartService.updateById(chart);
            }
            // channel为null时，chart_id传输失败，不requeue
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, Objects.nonNull(chart));
        }
    }

}

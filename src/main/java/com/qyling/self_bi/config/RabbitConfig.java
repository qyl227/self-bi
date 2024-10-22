package com.qyling.self_bi.config;

import com.qyling.self_bi.common.ErrorCode;
import com.qyling.self_bi.exception.ThrowUtils;
import com.qyling.self_bi.model.entity.Chart;
import com.qyling.self_bi.model.enums.ChartStatusEnum;
import com.qyling.self_bi.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@Slf4j
public class RabbitConfig {
    @Resource
    private ChartService chartService;

    // TODO：一个更好的实践是将其大部分代码写在生产者中
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        log.info("Connected to RabbitMQ Host: {}", connectionFactory.getHost());
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        // 配置 ConfirmCallback，处理发布确认
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Message confirmed successfully. CorrelationData: {}", correlationData);
            } else {
                if (correlationData != null) {
                    // 状态修改
                    Long chartId = Long.valueOf(correlationData.getId());
                    Chart chart = chartService.getById(chartId);
                    chart.setStatus(ChartStatusEnum.FAILED);
                    chart.setExecMessage("Message failed to deliver. Cause: " + cause);
                    boolean update = chartService.updateById(chart);
                    ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "数据库异常");
                }
                log.error("Message failed to deliver. Cause: {}", cause);
            }
        });
        // 开启此项，才会将无法路由的消息返回给生产者
        rabbitTemplate.setMandatory(true);
        // 配置 ReturnCallback，处理无法路由（即到达交换机但找不到队列）的消息
        rabbitTemplate.setReturnsCallback(returned -> {
            Long chartId = Long.valueOf(String.valueOf(returned.getMessage().getBody()));
            Chart chart = chartService.getById(chartId);
            chart.setStatus(ChartStatusEnum.FAILED);
            chart.setExecMessage(("Routing Key : [" + returned.getRoutingKey() + "] not found"));
            boolean update = chartService.updateById(chart);
            ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "数据库异常");

            log.error("Routing Key : [{}] not found", returned.getRoutingKey());
        });
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
}

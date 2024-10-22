package com.qyling.self_bi.mq;

import cn.hutool.json.JSONUtil;
import com.qyling.self_bi.common.ErrorCode;
import com.qyling.self_bi.exception.ThrowUtils;
import com.qyling.self_bi.model.entity.Chart;
import com.qyling.self_bi.model.enums.ChartStatusEnum;
import com.qyling.self_bi.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.util.UUID;

@Component
@Slf4j
public class MessageSupplier {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ChartService chartService;


    public void sendMessage(String queueName, Object messageBody, Long messageId) {
        // 发送消息
        rabbitTemplate.convertAndSend(queueName, messageBody, new CorrelationData(String.valueOf(messageId)));
    }
}

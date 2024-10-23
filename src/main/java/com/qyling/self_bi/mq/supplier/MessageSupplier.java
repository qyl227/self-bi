package com.qyling.self_bi.mq.supplier;

import com.qyling.self_bi.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MessageSupplier {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ChartService chartService;


    public void sendMessage(String queueName, Object messageBody, Long messageId) {
        rabbitTemplate.convertAndSend(queueName, messageBody, new CorrelationData(String.valueOf(messageId)));
    }


}

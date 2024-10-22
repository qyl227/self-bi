package com.qyling.self_bi.mq;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component
public class MessageSupplier {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String queueName, Object messageBody, Long messageId) {
        rabbitTemplate.convertAndSend(queueName, messageBody, new CorrelationData(String.valueOf(messageId)));
    }
}

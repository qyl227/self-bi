package com.qyling.self_bi.mq;

import com.qyling.self_bi.constant.MQConstant;
import com.qyling.self_bi.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class RabbitMQTests {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    void sendSimpleMessage() {
        User user = new User();
        user.setId(2L);
        user.setUserPassword("123456");
        rabbitAdmin.declareQueue(new Queue("simple"));
        rabbitTemplate.convertAndSend("simple", user);
    }

    @Test
    void sendDirectMessage() {
        rabbitTemplate.convertAndSend("test.direct", "red1", "execution", new CorrelationData(UUID.randomUUID().toString()));
    }

    @Test
    void sendGenChartByAI() {
        rabbitTemplate.convertAndSend("MQConstant.GEN_CHART_BY_AI_QUEUE_NAME", 1848676060711346177L);
    }
}

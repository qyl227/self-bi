package com.qyling.self_bi.mq.consumer;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 路由消费者（测试用）
 */
@Component
@Profile("dev")
public class DirectConsumer {
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1"),
            exchange = @Exchange(name = "test.direct"),
            key = {"blue", "green"}
    ))
    public void listenQueue1(String msg, AcknowledgeMode acknowledgeMode) {
        try {
            System.out.println("queue1: " + msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name = "test.direct"),
            key = {"blue", "red"}
    ))
    public void listenQueue2(String msg) {
        System.out.println("queue2: " + msg);
    }
}

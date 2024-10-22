package com.qyling.self_bi.mq.consumer;

import cn.hutool.json.JSONUtil;
import com.qyling.self_bi.model.entity.User;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SimpleConsumer {
    @RabbitListener(queues = "simple", ackMode = "MANUAL")
    public void listenSimpleMessage(Message message, Channel channel) throws IOException {
        try {
            User user = JSONUtil.toBean(new String(message.getBody()), User.class);
            log.info("user = " + user);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("消息处理成功，消费者已ack");
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            log.error("消息处理异常，消费者已nack");
            throw new RuntimeException(e);
        }
    }
}

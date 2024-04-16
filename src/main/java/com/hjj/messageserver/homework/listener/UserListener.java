package com.hjj.messageserver.homework.listener;

import com.hjj.messageserver.homework.dto.Chat;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Profile("client")
@Component
@RabbitListener(queues = "#{'user.'.concat('${spring.rabbitmq.username}')}")
public class UserListener {

    @RabbitHandler
    public void receiveString(String in) {
        System.out.println(" [x] Received String '" + in + "'");
    }

    @RabbitHandler
    public void receiveChat(Chat in,
                            @Headers Map<String, Object> headers,
                            @Header(name = AmqpHeaders.RECEIVED_ROUTING_KEY, required = false) String routingKey) {
        System.out.println(" [x] Received Chat '" + in.getBody() + "' with routing key " + routingKey);
    }
}

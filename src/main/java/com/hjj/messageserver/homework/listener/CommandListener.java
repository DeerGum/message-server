package com.hjj.messageserver.homework.listener;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.hjj.messageserver.homework.dto.Command;

import java.util.HashMap;
import java.util.Map;

@Profile("server")
@Component
@RabbitListener(queues = "command", containerFactory = "retryExchangeContainerFactory", ackMode = "MANUAL")
public class CommandListener {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    @Qualifier("roomExchange")
    private TopicExchange roomExchange;

    private final Map<String, FanoutExchange> roomExchanges = new HashMap<>();

    private void createRoom(String roomName) {
        FanoutExchange exchange = new FanoutExchange("room." + roomName);
        rabbitAdmin.declareExchange(exchange);
        roomExchanges.put("room." + roomName, exchange);

        Binding binding = BindingBuilder.bind(exchange)
                .to(roomExchange)
                .with("*.room." + roomName);
        rabbitAdmin.declareBinding(binding);
    }

    private void inviteRoom(String roomName, String userId) {
        FanoutExchange userExchange = new FanoutExchange("user." + userId);
        rabbitAdmin.declareExchange(userExchange);

        Binding binding = BindingBuilder.bind(userExchange)
                .to(roomExchange)
                .with("*.user." + userId);
        rabbitAdmin.declareBinding(binding);
    }
	
    @RabbitHandler
    public void receive(Command command) throws Exception {
        System.out.println(" [x] Received '" + command.getBody() + "'");
        String commandStr = command.getCommand();
        String[] arguments = command.getArguments();
        switch (commandStr) {
            case "create":
                createRoom(arguments[0]);
                break;
            case "invite":
                inviteRoom(arguments[0], arguments[1]);
                break;
            default:
                throw new Exception("Exception occurred at command");
        }
    }
}

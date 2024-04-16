package com.hjj.messageserver;

import com.hjj.messageserver.homework.dto.Chat;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Profile("client")
@Component
public class RabbitmqChatClientRunner implements CommandLineRunner {

	@Autowired
	private RabbitProperties rabbitProperties;

	@Autowired
	private RabbitTemplate template;


	@Override
	public void run(String... arg0) {
		try (Scanner scanner = new Scanner(System.in)) {
			while(true) {
				String message = scanner.nextLine();

				Chat chat = new Chat();
				chat.setBody(message);
				chat.setUserName(rabbitProperties.getUsername());

				this.template.convertAndSend("request", "chat.user." + rabbitProperties.getUsername(), chat);
			}
		}
	}
}

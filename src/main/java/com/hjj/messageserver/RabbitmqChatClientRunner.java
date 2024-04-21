package com.hjj.messageserver;

import com.hjj.messageserver.homework.dto.Chat;
import com.hjj.messageserver.homework.dto.Command;
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

				if (message.startsWith("/")) {
					String[] commandAndArgs = message.substring(1).split("\\s", 2);

					Command command = new Command();
					command.setBody(message);
					command.setCommand(commandAndArgs[0]);
					command.setArguments(commandAndArgs[1].split("\\s"));

					this.template.convertAndSend("chat", "*.user." + commandAndArgs[0], command);
				} else {
					Chat chat = new Chat();
					chat.setBody(message);
					chat.setUserName(rabbitProperties.getUsername());

					this.template.convertAndSend("chat", "chat.user." + rabbitProperties.getUsername(), chat);
				}
			}
		}
	}
}

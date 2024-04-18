package com.hjj.messageserver.homework.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.hjj.messageserver.homework.listener.HomeworkReceiver;
import com.hjj.messageserver.homework.listener.HomeworkSender;

@Profile({"hw"})
@Configuration
public class HomeworkConfig {

	@Bean
	public TopicExchange chatExchange() {
		return new TopicExchange("chat");
	}
	@Bean
	public TopicExchange userExchange() {
		return new TopicExchange("user");
	}
	@Bean
	public FanoutExchange roomExchange() {
		return new FanoutExchange("room");
	}
	
	@Bean
	public Binding bindingChatExchangeToUserExchange(@Qualifier("chatExchange") TopicExchange chatExchange,
			@Qualifier("userExchange") TopicExchange userExchange) {
		return BindingBuilder.bind(userExchange)
		    .to(chatExchange)
		    .with("*.user.#");
	}

	@Bean
	public Binding bindingChatExchangeToRoomExchange(@Qualifier("chatExchange") TopicExchange chatExchange,
			@Qualifier("roomExchange") FanoutExchange roomExchange) {
		return BindingBuilder.bind(roomExchange)
				.to(chatExchange)
				.with("*.room.#");
	}
	
	@Profile({"receiver", "server"})
	private static class ReceiverConfig {
		
		@Bean
		public RabbitAdmin amqpAdmin(ConnectionFactory connectionFactory) {
			return new RabbitAdmin(connectionFactory);
		}

		@Bean
		public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
			SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
			factory.setConnectionFactory(connectionFactory);
			factory.setPrefetchCount(1);
			factory.setConcurrentConsumers(1);
			factory.setMaxConcurrentConsumers(10);

			return factory;
		}
		
		@Bean
		public Queue deadLetterQueue() {
			return new Queue("dead-letter");
		}
		
		@Bean
		public HomeworkReceiver receiver() {
	 	 	return new HomeworkReceiver();
		}

	}
	
	@Profile("sender")
	@Bean
	public HomeworkSender sender() {
		return new HomeworkSender();
	}
}

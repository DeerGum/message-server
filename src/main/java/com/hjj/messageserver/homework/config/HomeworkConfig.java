package com.hjj.messageserver.homework.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"hw"})
@Configuration
public class HomeworkConfig {
	
	@Bean
	public TopicExchange requestExchange() {
		return new TopicExchange("request");
	}
	@Bean
	public TopicExchange chatExchange() {
		return new TopicExchange("chat");
	}
	@Bean
	public DirectExchange userExchange() {
		return new DirectExchange("user");
	}
	@Bean
	public DirectExchange roomExchange() {
		return new DirectExchange("room");
	}
	
	@Profile("receiver")
	private static class ReceiverConfig {

		@Bean
		public HomeworkReceiver receiver() {
	 	 	return new HomeworkReceiver();
		}

	    @Bean
	    public Queue commandQueue() {
	        return new Queue("command");
	    }

	    @Bean
	    public Queue userQueue() {
	        return new Queue("user");
	    }

	    @Bean
	    public Queue roomQueue() {
	        return new Queue("room");
	    }
		
		@Bean
		public Binding bindingRequestExchangeToCommandQueue(@Qualifier("requestExchange") TopicExchange requestExchange,
				@Qualifier("commandQueue") Queue commandQueue) {
			return BindingBuilder.bind(commandQueue)
			    .to(requestExchange)
			    .with("command.#");
		}

		@Bean
		public Binding bindingRequestExchangeToChatExchange(@Qualifier("requestExchange") TopicExchange requestExchange,
				@Qualifier("chatExchange") TopicExchange chatExchange) {
			return BindingBuilder.bind(chatExchange)
			    .to(requestExchange)
			    .with("chat.#");
		}

		@Bean
		public Binding bindingChatExchangeToUserExchange(@Qualifier("chatExchange") TopicExchange chatExchange,
				@Qualifier("userExchange") DirectExchange userExchange) {
			return BindingBuilder.bind(userExchange)
			    .to(chatExchange)
			    .with("*.user.#");
		}

		@Bean
		public Binding bindingChatExchangeToRoomExchange(@Qualifier("chatExchange") TopicExchange chatExchange,
				@Qualifier("roomExchange") DirectExchange roomExchange) {
			return BindingBuilder.bind(roomExchange)
					.to(chatExchange)
					.with("*.room.#");
		}

		@Bean
		public Binding bindingUserExchangeToUserQueue(@Qualifier("userExchange") DirectExchange userExchange,
				@Qualifier("userQueue")	Queue userQueue) {
			return BindingBuilder.bind(userQueue)
					.to(userExchange)
					.with("#");
		}

		@Bean
		public Binding bindingRoomTopicToRoomQueue(@Qualifier("roomExchange") DirectExchange roomExchange,
				@Qualifier("roomQueue")	Queue roomQueue) {
			return BindingBuilder.bind(roomQueue)
					.to(roomExchange)
					.with("#");
		}
	}
	
	@Profile("sender")
	@Bean
	public HomeworkSender sender() {
		return new HomeworkSender();
	}
}

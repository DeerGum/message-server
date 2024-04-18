package com.hjj.messageserver.homework.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"client"})
@Configuration
public class HomeworkClientConfig {
	
	@Autowired
	private RabbitProperties rabbitProperties;

	@Bean
	public RabbitAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	@Bean
	public MessageConverter messageConverter() {
		ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter(
				new Jackson2JsonMessageConverter());
		
		MessageConverter simple = (MessageConverter) new SimpleMessageConverter();
		converter.addDelegate("text/plain", simple);
		converter.addDelegate(null, simple);

		return converter;
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
			ConnectionFactory connectionFactory,
			MessageConverter messageConverter) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(messageConverter);
		factory.setPrefetchCount(3);
		factory.setConcurrentConsumers(3);
		factory.setMaxConcurrentConsumers(3);
		
		return factory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate(
			ConnectionFactory connectionFactory,
			MessageConverter messageConverter) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter);
		return template;
	}

	@Bean
	public Queue userQueue() {
		return QueueBuilder.durable("user." + rabbitProperties.getUsername())
				.deadLetterExchange("")
				.deadLetterRoutingKey("dead-letter")
				.build();
	}
	
	@Bean
	public FanoutExchange userIdExchange() {
		return new FanoutExchange("user." + rabbitProperties.getUsername());
	}
	
	@Bean
	public Binding bindingUserExchangeToUserIdExchange(@Qualifier("userExchange") TopicExchange userExchange,
			@Qualifier("UserIdExchange") FanoutExchange userIdExchange) {
		return BindingBuilder.bind(userIdExchange)
		    .to(userExchange)
		    .with("*.user." + rabbitProperties.getUsername());
	}

	@Bean
	public Binding bindingUserIdExchangeToUserQueue(@Qualifier("userIdExchange") FanoutExchange userIdExchange,
												  @Qualifier("userQueue") Queue userQueue) {
		return BindingBuilder.bind(userQueue)
				.to(userIdExchange);
	}
}

package com.hjj.messageserver.homework1;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"hw1"})
@Configuration
public class Homework1Config {
	
	@Bean
	public Queue hello() {
		return new Queue("hello");
	}

	@Profile("receiver")
	@Bean
	public Homwork1Receiver receiver() {
		return new Homwork1Receiver();
	}

	@Profile("sender")
	@Bean
	public Homework1Sender sender() {
		return new Homework1Sender();
	}
}

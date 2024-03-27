package com.hjj.messageserver.test1;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"test1"})
@Configuration
public class Test1Config {
	
	@Bean
	public Queue hello() {
		return new Queue("hello");
	}

	@Profile("receiver")
	@Bean
	public Test1Receiver receiver() {
		return new Test1Receiver();
	}

	@Profile("sender")
	@Bean
	public Test1Sender sender() {
		return new Test1Sender();
	}
}

package com.hjj.messageserver.homework.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class HomeworkSender {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private TopicExchange requestExchange;

	AtomicInteger index = new AtomicInteger(0);
	AtomicInteger count = new AtomicInteger(0);

	private final String[] keys = {"command.orange.rabbit", "chat.user.user1", "chat.room.fox",
			"chat.brown.user", "fox.room.rabbit", "chat.room.brown"};

	@Scheduled(fixedDelay = 1000, initialDelay = 500)
	public void send() throws JsonProcessingException {

		if (this.index.incrementAndGet() == keys.length) {
			this.index.set(0);
		}
		String key = keys[this.index.get()];
		Map<String, Object> body = new HashMap<>();
		body.put(key, this.count.incrementAndGet());

		String message = objectMapper.writeValueAsString(body);
		template.convertAndSend(requestExchange.getName(), key, message);
		System.out.println(" [x] Sent '" + message + "'");
	}
}

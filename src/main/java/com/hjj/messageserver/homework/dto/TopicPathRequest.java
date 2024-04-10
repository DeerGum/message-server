package com.hjj.messageserver.homework.dto;

import lombok.Getter;

@Getter
public class TopicPathRequest {
	String username;
	String vhost;
	String resource;
	String name;
	String permission;
	String routing_key;
}

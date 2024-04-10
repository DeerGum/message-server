package com.hjj.messageserver.homework.dto;

import lombok.Getter;

@Getter
public class ResourcePathRequest {
	String username;
	String vhost;
	String resource;
	String name;
	String permission;
}

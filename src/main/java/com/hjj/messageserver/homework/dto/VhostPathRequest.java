package com.hjj.messageserver.homework.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VhostPathRequest {
	String username;
	String vhost;
	String ip;
}

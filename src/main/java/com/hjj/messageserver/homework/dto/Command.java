package com.hjj.messageserver.homework.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class Command {
	
	String body;
	String command;
	String[] arguments;

}

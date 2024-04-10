package com.hjj.messageserver.homework.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hjj.messageserver.homework.dto.ResourcePathRequest;
import com.hjj.messageserver.homework.dto.TopicPathRequest;
import com.hjj.messageserver.homework.dto.UserPathRequest;
import com.hjj.messageserver.homework.dto.VhostPathRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Profile("server")
@Slf4j
@RestController
@RequestMapping("/rabbit/auth")
public class RabbitAuthController {

    @Autowired
    ObjectMapper objectMapper;

    static final String ALLOW_USER_NAME = "user";
    static final String ALLOW_USER_PWD = "pass";
    static final String ALLOW_VHOST = "chat";

    static Pattern pattern = Pattern.compile("^(chat|command)\\.\\w+");

    @GetMapping
    public String index() {
        return "ok";
    }

    @PostMapping(path = "/user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String postUser(UserPathRequest request) throws JsonProcessingException {
        log.info("postUser " + objectMapper.writeValueAsString(request));

        if (request.getUsername().startsWith(ALLOW_USER_NAME) && request.getPassword().equals(ALLOW_USER_PWD)) {
            return "allow";
        } else {
            return "deny";
        }
    }

    @PostMapping("/vhost")
    public String postVhost(VhostPathRequest request) throws JsonProcessingException {
        log.info("postVhost " + objectMapper.writeValueAsString(request));

        if (request.getUsername().startsWith(ALLOW_USER_NAME) && request.getVhost().equals(ALLOW_VHOST)) {
            return "allow";
        } else {
            return "deny";
        }
    }

    @PostMapping("/resource")
    public String postResource(ResourcePathRequest request) throws JsonProcessingException {
        log.info("postResource " + objectMapper.writeValueAsString(request));
        String resourceType = request.getResource();
        String username = request.getUsername();
        String vhostName = request.getVhost();
        String resourceName = request.getName();
        String permission = request.getPermission();

        if (username.startsWith(ALLOW_USER_NAME)
                && vhostName.equals(ALLOW_VHOST)) {
            if (resourceType.equals("exchange")) {
                if (resourceName.equals("request")
                        && List.of("configure", "write").contains(permission)) {
                    return "allow";
                } else if (resourceName.equals("user")
                        && List.of("read").contains(permission)) {
                    return "allow";
                } else if (resourceName.equals("amq.default")) {
                    return "allow";
                }
            } else if (resourceType.equals("queue")) {
                if (("user." + username).equals(resourceName)
                        && List.of("configure", "write", "read").contains(permission)) {
                    return "allow";
                }
            }
        }

        return "deny";
    }

    @PostMapping("/topic")
    public String postTopic(TopicPathRequest request) throws JsonProcessingException {
        log.info("postTopic " + objectMapper.writeValueAsString(request));

        String username = request.getUsername();
        String vhostName = request.getVhost();
        String resourceType = request.getResource();
        String resourceName = request.getName();
        String permission = request.getPermission();
        String routingKey = request.getRouting_key();

        if (username.startsWith(ALLOW_USER_NAME)
                && vhostName.equals("chat")) {
            if (resourceType.equals("topic")) {
                if (resourceName.equals("request")
                        && permission.equals("write")
                        && (routingKey == null || pattern.matcher(routingKey).find())) {
                    return "allow";
                } else if (resourceName.equals("user")
                        && permission.equals("read")
                        && (routingKey == null
                        || ("*.user." + username).equals(routingKey))) {
                    return "allow";
                }
            } else if (resourceType.equals("queue")) {
                if (resourceName.equals("user." + username)
                        && permission.equals("read")
                        && routingKey != null && pattern.matcher(routingKey).find()) {
                    return "allow";
                }
            }
        }

        return "deny";
    }
}
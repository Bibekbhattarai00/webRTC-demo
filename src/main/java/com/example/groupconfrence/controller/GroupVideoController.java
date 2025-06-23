package com.example.groupconfrence.controller;

import com.example.groupconfrence.service.GroupVideoService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class GroupVideoController {


    private final GroupVideoService groupVideoService;

    public GroupVideoController(GroupVideoService groupVideoService) {
        this.groupVideoService = groupVideoService;
    }


    @GetMapping
    public String index() {
        return "index";
    }

    @MessageMapping("/testServer")
    @SendTo("/topic/testServer")
    public String testServer(String test) {
        return test;
    }

    @MessageMapping("/addUser")
    public void addUser(String userId, SimpMessageHeaderAccessor headerAccessor) {
        groupVideoService.addUser(userId, headerAccessor.getSessionId());
    }

    @MessageMapping("/joinRoom")
    public void joinRoom(String message) {
        groupVideoService.joinRoom(message);
    }

    @MessageMapping("/leaveRoom")
    public void leaveRoom(String message) {
        groupVideoService.leaveRoom(message);
    }

    @MessageMapping("/offer")
    public void handleOffer(String message) {
        groupVideoService.handleOffer(message);
    }

    @MessageMapping("/answer")
    public void handleAnswer(String message) {
        groupVideoService.handleAnswer(message);
    }

    @MessageMapping("/candidate")
    public void handleCandidate(String message) {
        groupVideoService.handleCandidate(message);
    }

    @MessageMapping("/getRoomStats")
    public void getRoomStats(String userId) {
        groupVideoService.getRoomStats(userId);
    }
}


package com.example.groupconfrence.service;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManagementService {

    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
    private final UserConnectionService userConnectionService;

    public SessionManagementService(UserConnectionService userConnectionService) {
        this.userConnectionService = userConnectionService;
    }

    public void registerUserSession(String sessionId, String userId) {
        sessionUserMap.put(sessionId, userId);
        System.out.println("Registered session " + sessionId + " for user " + userId);
    }

    public String getUserIdFromSession(String sessionId) {
        return sessionUserMap.get(sessionId);
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        System.out.println("WebSocket connected: " + sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userId = sessionUserMap.remove(sessionId);

        if (userId != null) {
            System.out.println("User " + userId + " disconnected");
            userConnectionService.handleUserDisconnect(userId);
        } else {
            System.out.println("Unknown session disconnected: " + sessionId);
        }
    }

    public Map<String, String> getActiveSessions() {
        return new ConcurrentHashMap<>(sessionUserMap);
    }
}

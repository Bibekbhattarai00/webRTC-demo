package com.example.groupconfrence.config;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
class WebSocketEventListener {

//    private final GroupVideoController groupVideoController;
//
//    WebSocketEventListener(GroupVideoController groupVideoController) {
//        this.groupVideoController = groupVideoController;
//    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        System.out.println("WebSocket session disconnected: " + sessionId);
    }
}
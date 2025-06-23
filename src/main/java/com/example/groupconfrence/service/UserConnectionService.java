package com.example.groupconfrence.service;

import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserConnectionService {

    private final Set<String> activeUsers = ConcurrentHashMap.newKeySet();
    private final Map<String, Set<String>> rooms = new ConcurrentHashMap<>();
    private final Map<String, String> userRooms = new ConcurrentHashMap<>();


    private final SimpMessagingTemplate simpMessagingTemplate;

    public UserConnectionService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void addUser(String userId) {
        activeUsers.add(userId);
    }

    public void removeUser(String userId) {
        activeUsers.remove(userId);
    }

    public Set<String> getActiveUsers() {
        return activeUsers;
    }

    public Map<String, Set<String>> getRooms() {
        return rooms;
    }

    public Map<String, String> getUserRooms() {
        return userRooms;
    }

    public void handleUserDisconnect(String userId) {
        System.out.println("User disconnected: " + userId);
        removeUser(userId);
        leaveCurrentRoom(userId);
    }

    public void leaveCurrentRoom(String userId) {
        String currentRoom = userRooms.get(userId);
        if (currentRoom != null) {
            Set<String> participants = rooms.get(currentRoom);
            if (participants != null) {
                participants.remove(userId);

                for (String participant : participants) {
                    JSONObject notification = new JSONObject();
                    notification.put("userId", userId);
                    notification.put("roomId", currentRoom);
                    simpMessagingTemplate.convertAndSendToUser(participant, "/topic/userLeft", notification.toString());
                }

                if (participants.isEmpty()) {
                    rooms.remove(currentRoom);
                }
            }
            userRooms.remove(userId);
        }
    }

    public boolean areUsersInSameRoom(String user1, String user2) {
        return userRooms.get(user1) != null && userRooms.get(user1).equals(userRooms.get(user2));
    }
}

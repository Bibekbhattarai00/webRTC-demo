package com.example.groupconfrence.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GroupVideoService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SessionManagementService sessionManagementService;
    private final UserConnectionService userConnectionService;

    public void addUser(String userId, String sessionId) {
        userConnectionService.addUser(userId);
        sessionManagementService.registerUserSession(sessionId, userId);
    }

    public void joinRoom(String message) {
        try {
            JSONObject data = new JSONObject(message);
            String userId = data.getString("userId");
            String roomId = data.getString("roomId");

            userConnectionService.leaveCurrentRoom(userId);
            userConnectionService.getRooms().computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
            userConnectionService.getUserRooms().put(userId, roomId);

            Set<String> participants = userConnectionService.getRooms().get(roomId);
            JSONArray participantArray = new JSONArray(participants);

            JSONObject response = new JSONObject();
            response.put("roomId", roomId);
            response.put("participants", participantArray);

            simpMessagingTemplate.convertAndSendToUser(userId, "/topic/roomJoined", response.toString());

            for (String participant : participants) {
                if (!participant.equals(userId)) {
                    JSONObject notification = new JSONObject();
                    notification.put("userId", userId);
                    notification.put("roomId", roomId);
                    simpMessagingTemplate.convertAndSendToUser(participant, "/topic/userJoined", notification.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leaveRoom(String message) {
        try {
            JSONObject data = new JSONObject(message);
            String userId = data.getString("userId");
            userConnectionService.leaveCurrentRoom(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleOffer(String message) {
        try {
            JSONObject data = new JSONObject(message);
            String fromUser = data.getString("fromUser");
            String toUser = data.getString("toUser");

            if (userConnectionService.areUsersInSameRoom(fromUser, toUser)) {
                simpMessagingTemplate.convertAndSendToUser(toUser, "/topic/offer", message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleAnswer(String message) {
        try {
            JSONObject data = new JSONObject(message);
            String fromUser = data.getString("fromUser");
            String toUser = data.getString("toUser");

            if (userConnectionService.areUsersInSameRoom(fromUser, toUser)) {
                simpMessagingTemplate.convertAndSendToUser(toUser, "/topic/answer", message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCandidate(String message) {
        try {
            JSONObject data = new JSONObject(message);
            String fromUser = data.getString("fromUser");
            String toUser = data.getString("toUser");

            if (userConnectionService.areUsersInSameRoom(fromUser, toUser)) {
                simpMessagingTemplate.convertAndSendToUser(toUser, "/topic/candidate", message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRoomStats(String userId) {
        JSONObject stats = new JSONObject();
        stats.put("totalRooms", userConnectionService.getRooms().size());
        stats.put("totalUsers", userConnectionService.getActiveUsers().size());

        JSONObject roomDetails = new JSONObject();
        for (Map.Entry<String, Set<String>> entry : userConnectionService.getRooms().entrySet()) {
            roomDetails.put(entry.getKey(), new JSONArray(entry.getValue()));
        }

        stats.put("rooms", roomDetails);

        simpMessagingTemplate.convertAndSendToUser(userId, "/topic/roomStats", stats.toString());
    }
}

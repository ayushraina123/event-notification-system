package com.example.event_notification_system.services;

import com.example.event_notification_system.enums.EventType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class CallbackSender {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendSuccess(String eventId, EventType type, String callbackUrl) {
        Map<String, Object> body = new HashMap<>();
        body.put("eventId", eventId);
        body.put("status", "COMPLETED");
        body.put("eventType", type.toString());
        body.put("processedAt", Instant.now().toString());

        try {
            restTemplate.postForEntity(callbackUrl, body, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to send success callback for event " + eventId + ": " + e.getMessage());
        }
    }

    public void sendFailure(String eventId, EventType type, String error, String callbackUrl) {
        Map<String, Object> body = new HashMap<>();
        body.put("eventId", eventId);
        body.put("status", "FAILED");
        body.put("eventType", type.toString());
        body.put("errorMessage", error);
        body.put("processedAt", Instant.now().toString());

        try {
            restTemplate.postForEntity(callbackUrl, body, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to send failure callback for event " + eventId + ": " + e.getMessage());
        }
    }
}

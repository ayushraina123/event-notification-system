package com.example.event_notification_system.dtos;

import com.example.event_notification_system.enums.EventStatus;
import com.example.event_notification_system.enums.EventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventModel {

    private final String eventId;
    private final EventType eventType;
    private final JsonNode payload;
    private final String callbackUrl;
    private EventStatus status;
    private final Instant createdAt;
}

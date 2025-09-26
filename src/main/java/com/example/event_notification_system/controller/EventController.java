package com.example.event_notification_system.controller;

import com.example.event_notification_system.dtos.EventRequest;
import com.example.event_notification_system.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createEvent(@Valid @RequestBody EventRequest eventRequest) {
        String eventId = eventService.submitEvent(eventRequest);
        return ResponseEntity.ok(Map.of("eventId", eventId, "message", "Event accepted for processing."));
    }
}

package com.example.event_notification_system.controller;

import com.example.event_notification_system.dtos.EventRequest;
import com.example.event_notification_system.enums.EventType;
import com.example.event_notification_system.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testValidEventSubmission() throws Exception {
        EventRequest request = EventRequest.builder()
                .eventType(EventType.EMAIL)
                .payload(JsonNodeFactory.instance.objectNode().put("key", "value"))
                .callbackUrl("http://localhost/callback")
                .build();

        Mockito.when(eventService.submitEvent(any(EventRequest.class))).thenReturn("event-123");

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("event-123"))
                .andExpect(jsonPath("$.message").value("Event accepted for processing."));
    }

    @Test
    void testMissingPayloadFields() throws Exception {
        EventRequest request = EventRequest.builder()
                .eventType(null)
                .payload(null)
                .callbackUrl("")
                .build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidEventType() throws Exception {
        String payload = """
                {
                  "eventType": "INVALID_TYPE",
                  "payload": {"msg": "hi"},
                  "callbackUrl": "http://localhost/callback"
                }
                """;

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}

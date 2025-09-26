package com.example.event_notification_system.dtos;

import com.example.event_notification_system.enums.EventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventRequest {
    @NotNull(message = "Event Type cannot be null")
    private EventType eventType;

    @NotNull(message = "Payload cannot be empty")
    private JsonNode payload;

    @NotBlank(message = "CallBackUrl cannot be blank")
    private String callbackUrl;
}

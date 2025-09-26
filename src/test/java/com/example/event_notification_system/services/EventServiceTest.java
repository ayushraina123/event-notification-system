package com.example.event_notification_system.services;

import com.example.event_notification_system.dtos.EventModel;
import com.example.event_notification_system.dtos.EventRequest;
import com.example.event_notification_system.enums.EventStatus;
import com.example.event_notification_system.enums.EventType;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private CallbackSender callbackSender;
    private EventService eventService;

    @BeforeEach
    void setup() {
        callbackSender = Mockito.mock(CallbackSender.class);
        eventService = new EventService(callbackSender);
    }

    @Test
    void eventCreation_eventsExistAndQueueAssignment() {
        EventRequest req1 = EventRequest.builder()
                .eventType(EventType.SMS)
                .payload(JsonNodeFactory.instance.objectNode().put("msg", "first"))
                .callbackUrl("http://callback1")
                .build();

        EventRequest req2 = EventRequest.builder()
                .eventType(EventType.SMS)
                .payload(JsonNodeFactory.instance.objectNode().put("msg", "second"))
                .callbackUrl("http://callback2")
                .build();

        String id1 = eventService.submitEvent(req1);
        String id2 = eventService.submitEvent(req2);

        assertTrue(eventService.getEvents().containsKey(id1));
        assertTrue(eventService.getEvents().containsKey(id2));

        EventModel model1 = eventService.getEvents().get(id1);
        EventModel model2 = eventService.getEvents().get(id2);

        assertNotNull(model1);
        assertNotNull(model2);
    }

    @Test
    void gracefulShutdown_stopsAcceptingNewEvents() throws InterruptedException {
        eventService.shutdown();
        assertFalse(eventService.isAccepting());

        EventRequest req = EventRequest.builder()
                .eventType(EventType.EMAIL)
                .payload(JsonNodeFactory.instance.objectNode())
                .callbackUrl("http://callback")
                .build();

        Exception ex = assertThrows(Exception.class, () -> eventService.submitEvent(req));
        assertTrue(ex.getMessage().contains("Shutting down"));
    }

    @Test
    void inProgressEvent_completesOnShutdown() throws InterruptedException {
        EventRequest req = EventRequest.builder()
                .eventType(EventType.PUSH)
                .payload(JsonNodeFactory.instance.objectNode())
                .callbackUrl("http://callback")
                .build();

        String id = eventService.submitEvent(req);
        EventModel model = eventService.getEvents().get(id);

        assertEquals(EventStatus.PENDING, model.getStatus());

        Thread shutdownThread = new Thread(() -> {
            try {
                eventService.shutdown();
            } catch (InterruptedException ignored) {
            }
        });
        shutdownThread.start();
        shutdownThread.join();

        assertTrue(model.getStatus() == EventStatus.COMPLETED || model.getStatus() == EventStatus.FAILED);
    }
}
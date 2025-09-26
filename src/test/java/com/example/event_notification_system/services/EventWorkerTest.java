package com.example.event_notification_system.services;

import com.example.event_notification_system.dtos.EventModel;
import com.example.event_notification_system.enums.EventStatus;
import com.example.event_notification_system.enums.EventType;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventWorkerTest {

    @Test
    void eventProcessing_randomSuccessOrFailure() throws InterruptedException {
        CallbackSender callbackSender = Mockito.mock(CallbackSender.class);
        BlockingQueue<EventModel> queue = new LinkedBlockingQueue<>();

        EventModel event = new EventModel("1", EventType.PUSH,
                JsonNodeFactory.instance.objectNode(), "http://callback.com",
                EventStatus.PENDING, Instant.now());
        queue.offer(event);

        EventService service = Mockito.mock(EventService.class);
        Mockito.when(service.isAccepting()).thenReturn(false);
        Mockito.doAnswer(inv -> {
            event.setStatus((EventStatus) inv.getArgument(1));
            return null;
        }).when(service).updateStatus(Mockito.anyString(), Mockito.any());

        EventWorker worker = new EventWorker(queue, EventType.PUSH, service, callbackSender);
        Thread t = new Thread(worker);
        t.start();
        t.join();

        assertTrue(event.getStatus() == EventStatus.COMPLETED || event.getStatus() == EventStatus.FAILED);
        Mockito.verify(service, Mockito.atLeastOnce()).updateStatus(Mockito.anyString(), Mockito.any());
    }
}
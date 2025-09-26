package com.example.event_notification_system.services;

import com.example.event_notification_system.dtos.EventModel;
import com.example.event_notification_system.dtos.EventRequest;
import com.example.event_notification_system.enums.EventStatus;
import com.example.event_notification_system.enums.EventType;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Getter
public class EventService {

    private final EnumMap<EventType, BlockingQueue<EventModel>> queues = new EnumMap<>(EventType.class);
    private final ConcurrentMap<String, EventModel> events = new ConcurrentHashMap<>();
    private final List<Thread> workers = new ArrayList<>();
    private final CallbackSender callbackSender;
    private volatile boolean accepting = true;

    public EventService(CallbackSender callbackSender) {
        this.callbackSender = callbackSender;

        for (EventType type : EventType.values()) {
            queues.put(type, new LinkedBlockingQueue<>());
        }
        startWorkers();
    }

    public String submitEvent(EventRequest request) {
        if (!accepting) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Shutting down, not accepting new events");
        }

        String id = UUID.randomUUID().toString();
        EventModel model = new EventModel(
                id,
                request.getEventType(),
                request.getPayload(),
                request.getCallbackUrl(),
                EventStatus.PENDING,
                Instant.now()
        );

        events.put(id, model);
        queues.get(request.getEventType()).offer(model);
        return id;
    }

    private void startWorkers() {
        for (EventType type : EventType.values()) {
            BlockingQueue<EventModel> queue = queues.get(type);
            Thread t = new Thread(new EventWorker(queue, type, this, callbackSender));
            t.setName(type + "-worker");
            t.start();
            workers.add(t);
        }
    }

    public void updateStatus(String eventId, EventStatus status) {
        EventModel model = events.get(eventId);
        if (model != null) {
            model.setStatus(status);
        }
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        accepting = false;
        for (Thread t : workers) {
            t.join(30_000);
        }
    }
}

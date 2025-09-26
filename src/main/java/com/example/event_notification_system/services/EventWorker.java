package com.example.event_notification_system.services;

import com.example.event_notification_system.dtos.EventModel;
import com.example.event_notification_system.enums.EventStatus;
import com.example.event_notification_system.enums.EventType;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class EventWorker implements Runnable {

    private final BlockingQueue<EventModel> queue;
    private final EventType type;
    private final EventService service;
    private final CallbackSender callbackSender;
    private final Random random = new Random();

    public EventWorker(BlockingQueue<EventModel> queue, EventType type,
                       EventService service, CallbackSender callbackSender) {
        this.queue = queue;
        this.type = type;
        this.service = service;
        this.callbackSender = callbackSender;
    }

    @Override
    public void run() {
        try {
            while (service.isAccepting() || !queue.isEmpty()) {
                EventModel event = queue.poll(1, TimeUnit.SECONDS);
                if (event == null) continue;

                service.updateStatus(event.getEventId(), EventStatus.PROCESSING);

                long processingMillis = switch (type) {
                    case EMAIL -> 5000;
                    case SMS -> 3000;
                    case PUSH -> 2000;
                };
                Thread.sleep(processingMillis);

                boolean fail = random.nextInt(10) == 0;
                if (fail) {
                    service.updateStatus(event.getEventId(), EventStatus.FAILED);
                    callbackSender.sendFailure(event.getEventId(), type,
                            "Simulated processing failure", event.getCallbackUrl());
                } else {
                    service.updateStatus(event.getEventId(), EventStatus.COMPLETED);
                    callbackSender.sendSuccess(event.getEventId(), type, event.getCallbackUrl());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

# event-notification-system
A Java-based asynchronous event processing system built with Spring Boot. This system accepts three types of events — EMAIL, SMS, and PUSH notifications — and processes them in separate queues with dedicated worker threads, ensuring FIFO (first-in-first-out) order and non-blocking execution.

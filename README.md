# event-notification-system

A Spring Boot-based Event Notification System that manages, queues, and processes different types of events. The project supports Docker Compose for easy deployment and local development.

---

## Table of Contents
- [Features](#features)
- [Getting Started](#getting-started)
- [Docker Compose Setup](#docker-compose-setup)
- [Unit Testing](#unit-testing)
- [Assumptions and Notes](#assumptions-and-notes)
- [License](#license)

---

## Features
- Create and manage events with multiple types
- Separate queues per event type, processed in FIFO order
- Configurable via `application.properties` or environment variables
- Unit-tested core business logic using JUnit
- Dockerized application for quick setup

---

## Getting Started

1. **Clone the repository**
```bash
git clone https://github.com/ayushraina123/event-notification-system.git
cd event-notification-system
```

---

## Run the application locally

```bash
mvn spring-boot:run
```

---

## Docker Compose Setup

### Build and start containers
```bash
docker-compose up --build
```

---

### Stop containers
```bash
docker-compose down
```

---

### Access the application
The API will be available at: [http://localhost:8080](http://localhost:8080)  

You can modify ports or service names in the `docker-compose.yml` file as needed.

---

### Unit Testing

JUnit-based tests cover:  
- Event creation and validation  
- Queue assignment per event type  
- FIFO queue processing  

Run tests using:

```bash
mvn test
```

---

### Assumptions and Notes

- Each event type has a single queue instance.  
- Events are processed in the order they arrive (FIFO).  
- Docker Compose provides an isolated environment for local development.  
- This project focuses on backend event management; frontend integration is not included.

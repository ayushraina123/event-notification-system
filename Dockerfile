# --------------------
# Build stage
# --------------------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml first (dependency caching)
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the JAR (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# --------------------
# Run stage
# --------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/event-notification-system-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
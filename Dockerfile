# Stage 1: Build the Java application
FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

WORKDIR /opt/theHarvester


# Stage 3: Final runtime container with Java and Python
FROM openjdk:17-slim

WORKDIR /app

# Copy Java application from builder stage
COPY --from=builder /app/target/osint-0.0.1-SNAPSHOT.jar app.jar

# Copy theHarvester setup from python-env stage
COPY --from=python-env /opt/theHarvester /opt/theHarvester

# Expose the application's port
EXPOSE 8080

# Default entry point for the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 1: Build the Java application
FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Install Python and theHarvester
FROM python:3.10-slim AS python-env

WORKDIR /opt/theHarvester

# Install dependencies and clone theHarvester
RUN apt-get update && apt-get install -y git && \
    git clone https://github.com/laramies/theHarvester.git /opt/theHarvester && \
    pip install -r /opt/theHarvester/requirements.txt && \
    apt-get clean

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

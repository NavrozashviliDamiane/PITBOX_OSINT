# Stage 1: Build the Java application
FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Install Python and theHarvester
FROM python:3.9-slim AS harvester

WORKDIR /opt/theHarvester

# Install theHarvester dependencies
RUN apt-get update && \
    apt-get install -y git && \
    git clone https://github.com/laramies/theHarvester.git . && \
    pip install -r requirements.txt && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Stage 3: Final runtime container with Java and Python
FROM openjdk:17-slim

WORKDIR /app

# Install Python and dependencies for theHarvester
RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    pip3 install requests dnspython && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy Java application from builder stage
COPY --from=builder /app/target/osint-0.0.1-SNAPSHOT.jar app.jar

# Copy theHarvester from the second stage
COPY --from=harvester /opt/theHarvester /opt/theHarvester

# Set theHarvester directory as an environment variable
ENV PATH="/opt/theHarvester:$PATH"

# Expose the application's port
EXPOSE 8080

# Default entry point for the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]

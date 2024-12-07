FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM python:3.9-slim AS python-base

FROM openjdk:17-slim

WORKDIR /app

# Copy Python from the Python base image
COPY --from=python-base /usr/local /usr/local

# Install necessary system dependencies
RUN apt-get update && apt-get install -y \
    git \
    curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Ensure pip is up to date
RUN python3 -m pip install --upgrade pip

# Install theHarvester requirements
RUN pip install -r https://raw.githubusercontent.com/laramies/theHarvester/master/requirements.txt

# Clone theHarvester
RUN git clone https://github.com/laramies/theHarvester.git /opt/theHarvester

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/osint-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Set the entrypoint to run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
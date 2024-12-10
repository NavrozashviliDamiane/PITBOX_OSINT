# Stage 1: Build the Java application
FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Install Python 3.11 and theHarvester
FROM python:3.11-slim AS harvester

WORKDIR /opt/theHarvester

# Install system dependencies and Python tools
RUN apt-get update && \
    apt-get install -y git curl && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Clone theHarvester repository
RUN git clone https://github.com/laramies/theHarvester.git .

# Install dependencies with explicit versions
RUN pip install --no-cache-dir \
    aiodns==3.2.0 \
    aiofiles==24.1.0 \
    aiohttp==3.11.10 \
    aiomultiprocess==0.9.1 \
    aiosqlite==0.20.0 \
    beautifulsoup4==4.12.3 \
    censys==2.2.16 \
    certifi==2024.8.30 \
    dnspython==2.7.0 \
    fastapi==0.115.6 \
    lxml==5.3.0 \
    netaddr==1.3.0 \
    ujson==5.10.0 \
    playwright==1.49.0 \
    PyYAML==6.0.2 \
    python-dateutil==2.9.0.post0 \
    requests==2.32.3 \
    retrying==1.3.4 \
    shodan==1.31.0 \
    slowapi==0.1.9 \
    uvicorn==0.32.1 \
    uvloop==0.21.0 && \
    playwright install && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Stage 3: Final runtime container with Java and Python 3.11
FROM python:3.11-slim

# Install OpenJDK
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy Java application from builder stage
COPY --from=builder /app/target/osint-0.0.1-SNAPSHOT.jar app.jar

# Copy theHarvester from the second stage
COPY --from=harvester /opt/theHarvester /opt/theHarvester

# Set theHarvester directory in PATH
ENV PATH="/opt/theHarvester:$PATH"

# Expose the application's port
EXPOSE 8080

# Default entry point for the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 1: Build the Java application
FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime with Java and Python 3.10+
FROM openjdk:17-slim

WORKDIR /app

# Install Python 3.10 from Debian backports
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    gnupg \
    git && \
    echo "deb http://deb.debian.org/debian bullseye-backports main" >> /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y -t bullseye-backports python3.10 python3.10-distutils python3-pip && \
    apt-get clean

# Verify Python installation
RUN python3.10 --version

# Link python3.10 as the default Python
RUN ln -sf /usr/bin/python3.10 /usr/bin/python

# Clone theHarvester repository and install dependencies
RUN git clone https://github.com/laramies/theHarvester.git /opt/theHarvester
RUN pip install -r /opt/theHarvester/requirements.txt

# Copy the built Java application
COPY --from=builder /app/target/osint-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8080

# Start the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]

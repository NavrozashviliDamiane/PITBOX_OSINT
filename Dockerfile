FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-slim

WORKDIR /app

# Install dependencies and build tools
RUN apt-get update && apt-get install -y \
    software-properties-common \
    build-essential \
    git \
    curl \
    wget \
    zlib1g-dev \
    libssl-dev \
    libbz2-dev \
    libreadline-dev \
    libsqlite3-dev \
    && apt-get clean




# Verify Python and pip installation
RUN python3.10 --version
RUN pip --version

# Clone theHarvester and install dependencies
RUN git clone https://github.com/laramies/theHarvester.git /opt/theHarvester
RUN pip install -r /opt/theHarvester/requirements.txt

# Test theHarvester
RUN python3.10 /opt/theHarvester/theHarvester.py --help

# Copy the built JAR file
COPY --from=builder /app/target/osint-0.0.1-SNAPSHOT.jar app.jar

# Expose the port for the application
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

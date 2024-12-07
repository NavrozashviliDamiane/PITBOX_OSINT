FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-slim

WORKDIR /app
RUN apt-get update && apt-get install -y \
    wget \
    gpg \
    ca-certificates \
    python3 \
    python3-pip \
    git \
    curl && \
    apt-get clean

# Download and install specific Python version if needed
RUN wget https://www.python.org/ftp/python/3.9.16/Python-3.9.16.tgz \
    && tar -xzf Python-3.9.16.tgz \
    && cd Python-3.9.16 \
    && ./configure --enable-optimizations \
    && make -j $(nproc) \
    && make altinstall \
    && cd .. \
    && rm -rf Python-3.9.16.tgz Python-3.9.16

# Ensure pip is installed for the specific Python version
RUN python3.9 -m ensurepip --upgrade

# Install theHarvester requirements
# RUN python3.9 -m pip install -r https://raw.githubusercontent.com/laramies/theHarvester/master/requirements.txt
RUN git clone https://github.com/laramies/theHarvester.git /opt/theHarvester

COPY --from=builder /app/target/osint-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
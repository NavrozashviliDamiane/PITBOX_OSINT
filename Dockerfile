FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-slim

WORKDIR /app
RUN apt-get update && apt-get install -y \
    python3.10 \
    python3-pip \
    git \
    curl && \
    apt-get clean

RUN ln -sf /usr/bin/python3.10 /usr/bin/python3
RUN pip install -r https://raw.githubusercontent.com/laramies/theHarvester/master/requirements.txt
RUN git clone https://github.com/laramies/theHarvester.git /opt/theHarvester

RUN python3 --version
RUN python3 /opt/theHarvester/theHarvester.py --help

COPY --from=builder /app/target/osint-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

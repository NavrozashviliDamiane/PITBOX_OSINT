FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM python:3.9-slim

WORKDIR /opt/theHarvester

RUN apt-get update && apt-get install -y git curl && apt-get clean

RUN git clone https://github.com/laramies/theHarvester.git /opt/theHarvester

RUN pip install -r /opt/theHarvester/requirements.txt

RUN pip install flask

COPY --from=builder /app/target/osint-0.0.1-SNAPSHOT.jar app.jar

COPY api.py /opt/theHarvester/

EXPOSE 8080

# You might need to adjust the entrypoint depending on how you want to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]
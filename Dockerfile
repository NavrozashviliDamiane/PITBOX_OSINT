FROM openjdk:17-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    python \
    python-pip \
    git \
    curl && \
    apt-get clean

RUN pip install -r https://raw.githubusercontent.com/laramies/theHarvester/master/requirements.txt

RUN git clone https://github.com/laramies/theHarvester.git /opt/theHarvester

COPY target/osint-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

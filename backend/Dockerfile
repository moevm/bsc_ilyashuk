FROM ubuntu:18.04

RUN mkdir /app
WORKDIR /app

COPY ./requirements.txt /app/requirements.txt
COPY ./build/libs/bsc_backend.jar /app/bsc_backend.jar
COPY ./saved_model /app/saved_model
COPY ./extract_features.py /app/extract_features.py

RUN apt-get update && \
    apt-get install -y default-jdk python3-pip python3-dev libsndfile1-dev ffmpeg && \
    apt-get clean

RUN pip3 install -r requirements.txt

CMD ["java", "-XX:+UseContainerSupport", "-jar",  "bsc_backend.jar"]
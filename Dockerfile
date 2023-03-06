#FROM maven:3.5-jdk-11 AS build
#COPY src /usr/src/app/src
#COPY pom.xml /usr/src/app
#RUN mvn -f /usr/src/app/pom.xml clean package
#
#FROM gcr.io/distroless/java
#COPY --from=build /usr/src/app/target/tg_bot_holidays-0.0.1-SNAPSHOT.jar /usr/app/tg_bot_holidays-0.0.1-SNAPSHOT.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/usr/app/tg_bot_holidays-0.0.1-SNAPSHOT.jar"]

FROM adoptopenjdk/openjdk11:alpine-jre
LABEL maintainer="sntc4"
WORKDIR /myapp
COPY target/tg_bot_holidays-0.0.1-SNAPSHOT.jar /myapp/my-app.jar
ENTRYPOINT ["java","-jar","my-app.jar"]
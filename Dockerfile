FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./user_managementREST/target/user_managementREST-1.0-SNAPSHOT.jar /app

EXPOSE 8082

CMD ["java", "-jar", "user_managementREST-1.0-SNAPSHOT.jar"]
FROM maven:3.9.10-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/iTicket-0.0.1-SNAPSHOT.jar .
EXPOSE 8080 5432 443
ENTRYPOINT ["java", "-jar", "iTicket-0.0.1-SNAPSHOT.jar"]
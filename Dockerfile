FROM maven:3.9.10-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/iTicket-0.0.1-SNAPSHOT.jar .
EXPOSE 8080 5432 443
ENTRYPOINT ["java", "-jar", "iTicket-0.0.1-SNAPSHOT.jar"]
FROM maven:3.9.10-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# ===== Runtime Stage =====
FROM gcr.io/distroless/java17:nonroot
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/abhyudaya-0.2.1.jar app.jar

# Expose only the app port
EXPOSE 8080

# Run as non-root (distroless nonroot is default)
USER nonroot

ENTRYPOINT ["java","-jar","app.jar"]

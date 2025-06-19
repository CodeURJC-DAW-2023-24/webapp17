# Stage 1: Build with Maven and Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy source code and build the JAR
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Final image
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar


# Expose application port
EXPOSE 8443

# Run the Spring Boot application
ENTRYPOINT ["sh", "-c", "sleep 10 && java -jar app.jar"]


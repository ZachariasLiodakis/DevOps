# Stage 1: Build the Spring Boot app
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build
COPY Akinita/pom.xml .
COPY Akinita/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:24-rc-oracle
WORKDIR /app
COPY --from=builder /build/target/ds-lab-2024-0.0.1-SNAPSHOT.jar ./application.jar
EXPOSE 8080
CMD ["java","-jar","application.jar"]
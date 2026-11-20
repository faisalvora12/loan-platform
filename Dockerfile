# Stage 1: build the jar with Maven + JDK
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom first and download dependencies: this layer is cached until pom.xml changes
COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

# Stage 2: run it on a JRE only (smaller image, no Maven or source code)
FROM eclipse-temurin:17-jre
WORKDIR /app

# Don't run as root
RUN useradd --system app
USER app

COPY --from=build /app/target/loan-platform-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

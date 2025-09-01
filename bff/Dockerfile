# Stage 1: Build the application using a JDK 17 image
FROM gradle:8.8-jdk17 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN ./gradlew bootJar --no-daemon

# Stage 2: Create the final image using a JRE 17 image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

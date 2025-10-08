FROM gradle:8.10.1-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew --no-daemon :event-management-service:bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# якщо в Gradle задав: bootJar { archiveFileName = 'app.jar' }
COPY --from=build /workspace/event-management-service/build/libs/app.jar /app/app.jar
# або без фікса імені:
# COPY --from=build /workspace/event-management-service/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

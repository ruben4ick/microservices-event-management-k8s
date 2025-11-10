FROM gradle:8.10.1-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew --no-daemon :admin-telemetry-service:bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/admin-telemetry-service/build/libs/app.jar /app/app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]

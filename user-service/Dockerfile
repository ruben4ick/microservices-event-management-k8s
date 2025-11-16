FROM gradle:8.10.1-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew --no-daemon :user-service:bootJar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/user-service/build/libs/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

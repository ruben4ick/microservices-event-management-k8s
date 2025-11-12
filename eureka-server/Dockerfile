FROM gradle:8.10.1-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew --no-daemon :eureka-server:bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/eureka-server/build/libs/app.jar /app/app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]

# 1. Build
FROM gradle:8.10.1-jdk21 AS builder

WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || return 0

COPY . .
RUN ./gradlew bootJar --no-daemon

# 2. Export
FROM scratch AS export
COPY --from=builder /app/build/libs/app.jar /

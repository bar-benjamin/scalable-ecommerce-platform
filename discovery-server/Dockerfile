# --- Stage 1: build ---
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle .
COPY build.gradle .
COPY discovery-server/build.gradle discovery-server/

RUN chmod +x gradlew
RUN ./gradlew :discovery-server:dependencies --no-daemon

COPY discovery-server/src discovery-server/src

RUN ./gradlew :discovery-server:bootJar --no-daemon -x test

# --- Stage 2: runtime ---
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Non-root user — least privilege principle for production containers
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=builder /app/discovery-server/build/libs/discovery-server.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
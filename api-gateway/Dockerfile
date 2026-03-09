FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle .
COPY build.gradle .
COPY api-gateway/build.gradle api-gateway/

RUN chmod +x gradlew
RUN ./gradlew :api-gateway:dependencies --no-daemon

COPY api-gateway/src api-gateway/src

RUN ./gradlew :api-gateway:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=builder /app/api-gateway/build/libs/api-gateway.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
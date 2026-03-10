FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle .
COPY build.gradle .
COPY payment-service/build.gradle payment-service/

RUN chmod +x gradlew
RUN ./gradlew :payment-service:dependencies --no-daemon

COPY payment-service/src payment-service/src

RUN ./gradlew :payment-service:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=builder /app/payment-service/build/libs/payment-service.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
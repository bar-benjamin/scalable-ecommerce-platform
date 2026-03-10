FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle .
COPY build.gradle .
COPY order-service/build.gradle order-service/

RUN chmod +x gradlew
RUN ./gradlew :order-service:dependencies --no-daemon

COPY order-service/src order-service/src

RUN ./gradlew :order-service:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=builder /app/order-service/build/libs/order-service.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle .
COPY build.gradle .

COPY discovery-server/build.gradle    discovery-server/
COPY api-gateway/build.gradle         api-gateway/
COPY user-service/build.gradle        user-service/
COPY product-service/build.gradle     product-service/
COPY cart-service/build.gradle        cart-service/
COPY order-service/build.gradle       order-service/
COPY payment-service/build.gradle     payment-service/
COPY notification-service/build.gradle notification-service/

RUN chmod +x gradlew
RUN ./gradlew :order-service:dependencies --no-daemon

COPY order-service/src order-service/src
RUN ./gradlew :order-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
COPY --from=builder /app/order-service/build/libs/order-service.jar app.jar
USER appuser
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
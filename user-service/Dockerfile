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
RUN ./gradlew :user-service:dependencies --no-daemon

COPY user-service/src user-service/src
RUN ./gradlew :user-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
COPY --from=builder /app/user-service/build/libs/user-service.jar app.jar
USER appuser
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
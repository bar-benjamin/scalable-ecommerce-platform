FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle .
COPY build.gradle .
COPY cart-service/build.gradle cart-service/

RUN chmod +x gradlew
RUN ./gradlew :cart-service:dependencies --no-daemon

COPY cart-service/src cart-service/src

RUN ./gradlew :cart-service:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=builder /app/cart-service/build/libs/cart-service.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
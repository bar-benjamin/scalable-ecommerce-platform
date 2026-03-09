# E-Commerce Platform

Microservices e-commerce platform built with Java, Spring Boot and Spring Cloud
## Architecture
```
                        ┌──────────────────────┐
                        │      api-gateway      │
                        │  (Spring Cloud GW)    │
                        │  - routing            │
                        │  - JWT auth filter    │
                        │  - rate limiting      │
                        └──────────┬────────────┘
                                   │ routes (REST)
              ┌────────────────────┼────────────────────┐
              │                    │                     │
              ▼                    ▼                     ▼
   ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
   │   user-service   │  │ product-service  │  │   cart-service   │
   │ - registration   │  │ - catalog        │  │ - cart CRUD      │
   │ - JWT auth       │  │ - inventory      │  │ - item quantities│
   │ - profiles       │  │ - categories     │  │ - cart expiry    │
   └──────────────────┘  └──────────────────┘  └──────────────────┘
              │ Feign (REST)                        │ Feign (REST)
              └──────────────────┬──────────────────┘
                                 ▼
                    ┌────────────────────────┐
                    │      order-service     │
                    │  - order lifecycle     │
                    │  - status tracking     │
                    │  - order history       │
                    └────────────┬───────────┘
                                 │ Kafka: order-placed
                    ┌────────────┴────────────┐
                    ▼                         ▼
       ┌─────────────────────┐   ┌──────────────────────────┐
       │   payment-service   │   │   notification-service   │
       │ - Stripe SDK        │   │ - SendGrid (email)       │
       │ - charge / refund   │   │ - order confirmations    │
       └──────────┬──────────┘   │ - shipping updates       │
                  │              └──────────────────────────┘
                  │ Kafka: payment-confirmed
                  ▼
       ┌─────────────────────┐
       │     order-service   │  (status update: PAID)
       └─────────────────────┘

  ┌──────────────────────────────────────────────────────────────┐
  │                  discovery-server (Eureka)                   │
  │  all services register here — gateway resolves via Eureka    │
  └──────────────────────────────────────────────────────────────┘

  ┌──────────────────────────────────────────────────────────────┐
  │             Observability & Infrastructure                   │
  │  Prometheus -> scrapes /actuator/prometheus -> all services  │
  │  Grafana    -> dashboards                   -> Prometheus    │
  │  ELK Stack  -> aggregates logs              -> all services  │
  │  Kafka topics: order-placed                                  │
  │                payment-confirmed                             │
  │                notification-trigger                          │
  └──────────────────────────────────────────────────────────────┘
```

## Tech Stack

| Concern               | Technology                          |
|-----------------------|-------------------------------------|
| Language              | Java 17 (JDK 21 toolchain)          |
| Framework             | Spring Boot 3.3.5                   |
| Service mesh          | Spring Cloud 2023.0.3               |
| Build                 | Gradle (Groovy DSL)                 |
| Service discovery     | Eureka (Spring Cloud Netflix)       |
| API Gateway           | Spring Cloud Gateway                |
| Sync communication    | OpenFeign (REST)                    |
| Async communication   | Apache Kafka                        |
| Database              | PostgreSQL (per-service schema)     |
| Payments              | Stripe Java SDK                     |
| Email                 | SendGrid Java SDK                   |
| Containerization      | Docker + Docker Compose             |
| Monitoring            | Prometheus + Grafana                |
| Logging               | ELK Stack                           |
| CI/CD                 | GitHub Actions                      |

## Services

| Service                | Port  | Responsibility                            |
|------------------------|-------|-------------------------------------------|
| `discovery-server`     | 8761  | Eureka service registry                   |
| `api-gateway`          | 8080  | Routing, JWT validation, rate limiting    |
| `user-service`         | 8081  | Registration, JWT issuance, profiles      |
| `product-service`      | 8082  | Catalog, categories, inventory            |
| `cart-service`         | 8083  | Cart CRUD, quantities, scheduled expiry   |
| `order-service`        | 8084  | Order lifecycle, Kafka producer/consumer  |
| `payment-service`      | 8085  | Stripe charges, refunds, webhooks         |
| `notification-service` | 8086  | SendGrid email, Kafka consumer            |

## Getting Started

### Prerequisites
- JDK 21
- Docker + Docker Compose
- Gradle 8+

### Run
```bash
# Start the full stack
docker compose up -d

# Or run a single service from the monorepo root
./gradlew :user-service:bootRun
```

### Build
```bash
./gradlew build
```

## Project Structure
```
ecommerce-platform/
├── api-gateway/
├── cart-service/
├── discovery-server/
├── notification-service/
├── order-service/
├── payment-service/
├── product-service/
├── user-service/
├── build.gradle          # configuration
├── settings.gradle       # modules
└── .gitignore
```
package com.ecommerce.gateway.filter;

import com.ecommerce.gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/payments/webhook",
            "/actuator"
    );

    private final JwtUtil jwt_util;

    public AuthenticationFilter(JwtUtil jwt_util) {
        this.jwt_util = jwt_util;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // CORS preflight — never carries auth headers, always let through
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        if (path.startsWith("/api/products") && exchange.getRequest().getMethod() == HttpMethod.GET) {
            return chain.filter(exchange);
        }

        String auth_header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (auth_header == null || !auth_header.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String bearer_token = auth_header.substring(7);

        if (!jwt_util.isTokenValid(bearer_token)) {
            return unauthorized(exchange);
        }

        ServerWebExchange mutated_exchange = exchange.mutate()
                .request(r -> r
                        .headers(h -> {
                            h.remove("X-User-Id");
                            h.remove("X-User-Role");
                            h.remove("X-User-Email");
                        })
                        .header("X-User-Id",    jwt_util.extractUserId(bearer_token))
                        .header("X-User-Role",  jwt_util.extractRole(bearer_token))
                        .header("X-User-Email", jwt_util.extractEmail(bearer_token)))
                .build();

        return chain.filter(mutated_exchange);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
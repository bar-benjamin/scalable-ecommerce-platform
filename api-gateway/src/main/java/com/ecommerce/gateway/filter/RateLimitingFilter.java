package com.ecommerce.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {
    @Override
    public int getOrder() {
        // Runs first before the auth filter so we can reject clearly abusive clients
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private final int capacity;
    private final int refill_tokens;
    private final long refill_period_millis;

    // Each entry holds [available_tokens, last_refill_timestamp_ms].
    // ConcurrentHashMap for thread safety across the reactive event loop threads.
    // Example: "192.168.1.42" -> [8, 1741515600000]
    private final ConcurrentHashMap<String, AtomicLong[]> buckets = new ConcurrentHashMap<>();

    public RateLimitingFilter(
            @Value("${rate-limiter.capacity}") int capacity,
            @Value("${rate-limiter.refill-tokens}") int refill_tokens,
            @Value("${rate-limiter.refill-period-seconds}") int refill_period_seconds) {
        this.capacity = capacity;
        this.refill_tokens = refill_tokens;
        this.refill_period_millis = refill_period_seconds * 1000L;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String client_ip = resolveClientIp(exchange);

        if (!tryConsume(client_ip)) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("Retry-After",
                    String.valueOf(refill_period_millis / 1000));
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    // Figures out the real client IP. If there's an X-Forwarded-For header (set by a load balancer)
    // it uses the first IP in that list. Otherwise, falls back to the direct connection IP.
    private String resolveClientIp(ServerWebExchange exchange) {
        String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        var remote_address = exchange.getRequest().getRemoteAddress();
        return remote_address != null ? remote_address.getAddress().getHostAddress() : "unknown";
    }

    // It tracks how many requests each client IP has made using a token bucket algorithm.
    // Each IP gets a bucket of tokens, each request consumes one, and tokens refill periodically
    private boolean tryConsume(String client_ip) {
        long now = Instant.now().toEpochMilli();
        AtomicLong[] bucket = buckets.computeIfAbsent(client_ip, k -> new AtomicLong[]{
                new AtomicLong(capacity),
                new AtomicLong(now)
        });

        long last_refill = bucket[1].get();
        long elapsed = now - last_refill;
        long tokens_to_add = (elapsed / refill_period_millis) * refill_tokens;

        if (tokens_to_add > 0 && bucket[1].compareAndSet(last_refill, now)) {
            bucket[0].updateAndGet(t -> Math.min(capacity, t + tokens_to_add));
        }

        long prev;
        do {
            prev = bucket[0].get();
            if (prev <= 0) return false;
        } while (!bucket[0].compareAndSet(prev, prev - 1));

        return true;
    }
}
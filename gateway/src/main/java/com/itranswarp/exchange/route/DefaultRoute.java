package com.itranswarp.exchange.route;


import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

//这里需要设置Configuration而不是Component才能把RedisRateLimiter初始化好（虽然他们都是调用方法获得的RedisRateLimiter）
@Configuration
public class DefaultRoute {

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just("all");
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder, @Value("#{exchangeConfiguration.apiEndpoints.tradingApi}") String tradingApi) {
        String httpUri = tradingApi;
        return builder.routes()
                .route("circuitbreaker_route",
                        r -> r.path("/**")
                                .filters(f -> f
                                        .requestRateLimiter().configure(
                                                c -> c.setRateLimiter(redisRateLimiter())
                                        )
                                        .circuitBreaker(c -> c.setName("myCircuitBreaker").setFallbackUri("/defaultFallback"))
                                        .rewritePath("/ttt", "/cff")


                                )
                                .uri("http://localhost:8001"))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 2);
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(2)).build()).build());
    }
}

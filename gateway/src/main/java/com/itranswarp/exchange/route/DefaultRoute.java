package com.itranswarp.exchange.route;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DefaultRoute {
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder, @Value("#{exchangeConfiguration.apiEndpoints.tradingApi}") String tradingApi) {
        String httpUri = tradingApi;
        return builder.routes()
                .route("circuitbreaker_route",
                        r -> r.path("/**")
                                .filters(f -> f
//                                                .circuitBreaker(c -> c.setName("myCircuitBreaker").setFallbackUri("forward:/inCaseOfFailureUseThis"))
                                        .rewritePath("/ttt", "/cff")
                                )
                                .uri("http://localhost:8001"))
                .build();
    }


}

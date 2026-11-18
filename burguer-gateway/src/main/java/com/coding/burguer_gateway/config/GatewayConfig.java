package com.coding.burguer_gateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> pedidosRoute() {
        return GatewayRouterFunctions.route("burger-productor-route")
                .route(RequestPredicates.path("/api/pedidos/**"), HandlerFunctions.http())
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("pedidosCB", URI.create("forward:/fallback/pedidos")))
                .filter(lb("burger-productor"))
                .build();
    }
}

package com.bschev.reactive.functional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RoutingConfiguration {

    @Bean
    public RouterFunction<ServerResponse> routerFunctionData(DataHandler dataHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/functional/data/{id}")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), dataHandler::data);
    }

}

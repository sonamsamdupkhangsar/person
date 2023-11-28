package org.mycompany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class Router {

    private static final Logger LOG = LoggerFactory.getLogger(Router.class);

    @Bean
    public RouterFunction<ServerResponse> route(Handler handler) {
        return RouterFunctions.route(GET("/persons/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::getPerson)
        .andRoute(POST("/persons").and(accept(MediaType.APPLICATION_JSON)), handler::createNew)
                .andRoute(PUT("/persons").and(accept(MediaType.APPLICATION_JSON)), handler::save)
                .andRoute(GET("/persons").and(accept(MediaType.APPLICATION_JSON)), handler::getPageOfPeople);
    }
}

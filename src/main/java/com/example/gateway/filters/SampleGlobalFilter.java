package com.example.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
//import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    // Definición de colores
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        logger.info(ANSI_GREEN + "Ejecutando el filtro antes del request PRE" + ANSI_RESET);

        // Mutar la solicitud y agregar el encabezado "token"
        ServerWebExchange mutatedExchange = exchange.mutate()
            .request(exchange.getRequest().mutate()
                .headers(h -> h.add("token", "abcdefg"))
                .build())
            .build();

        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            logger.info(ANSI_YELLOW + "Ejecutando filtro POST response" + ANSI_RESET);
            String token = mutatedExchange.getRequest().getHeaders().getFirst("token");
            if (token != null) {
                logger.info(ANSI_RED + "Token: " + token + ANSI_RESET);
                mutatedExchange.getResponse().getHeaders().add("token", token);
            }

            Optional.ofNullable(mutatedExchange.getRequest().getHeaders().getFirst("token"))
            .ifPresent(value -> {
                logger.info(ANSI_RED + "Token2: " + value + ANSI_RESET);
                mutatedExchange.getResponse().getHeaders().add("token2", value);
            });

            mutatedExchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
            //mutatedExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        }));
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
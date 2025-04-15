package com.example.gateway.filters.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class SampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory<SampleCookieGatewayFilterFactory.ConfigCookie> {

    private final Logger logger = LoggerFactory.getLogger(SampleCookieGatewayFilterFactory.class);

    public SampleCookieGatewayFilterFactory() {
        super(ConfigCookie.class);
    }
    @Override
    public GatewayFilter apply(ConfigCookie config) {
        logger.info("Ejecutando el PRE filtro de cookies con configuración: {}", config.getMessage());
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            logger.info("Ejecutando el POST filtro de cookies con configuración: {}", config.getMessage());
            exchange.getResponse().getCookies().add("name", ResponseCookie.from(config.name, config.value).build());
        }));
    }

    public static class ConfigCookie {
        // Configuración adicional si es necesario
        private String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        private String value;
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        private String message;
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
}

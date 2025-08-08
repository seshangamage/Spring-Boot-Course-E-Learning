package com.example.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Component
@Order(-2)
public class ErrorHandlerConfig extends AbstractErrorWebExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlerConfig.class);

    public ErrorHandlerConfig(ErrorAttributes errorAttributes, 
                            ApplicationContext applicationContext,
                            ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), request -> {
            Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());
            Throwable error = errorAttributes.getError(request);
            
            logger.error("Gateway Error occurred: {}", error.getMessage());
            
            int status = Optional.ofNullable(errorPropertiesMap.get("status"))
                .map(s -> Integer.parseInt(s.toString()))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of(
                    "status", status,
                    "message", "Service temporarily unavailable. Please try again later.",
                    "error", error.getMessage(),
                    "path", request.path()
                )));
        });
    }
}

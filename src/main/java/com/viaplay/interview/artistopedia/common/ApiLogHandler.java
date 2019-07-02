package com.viaplay.interview.artistopedia.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Component
public class ApiLogHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiLogHandler.class);

    public static ExchangeFilterFunction logRequest() {

        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    public static ExchangeFilterFunction logResponse() {

        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Reponse status code {} ", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }


}


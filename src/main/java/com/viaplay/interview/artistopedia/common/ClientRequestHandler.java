package com.viaplay.interview.artistopedia.common;

import com.viaplay.interview.artistopedia.exception.RestException;
import com.viaplay.interview.artistopedia.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ClientRequestHandler {


    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<?> executeRequestMono(String uriPath, Class<?> clazz) {

        System.out.println("uriPath" + uriPath + " :clazz:" + clazz);
        return  webClientBuilder.build().get().uri(uriPath).
                retrieve().
                onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RestException(ServiceException.NOT_FOUND))).
                onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RestException(ServiceException.INTERNAL_SERVER_ERROR)))
                .bodyToMono(clazz).retry(1);
    }



}

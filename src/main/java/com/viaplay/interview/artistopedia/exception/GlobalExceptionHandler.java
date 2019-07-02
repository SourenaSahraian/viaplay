package com.viaplay.interview.artistopedia.exception;

import com.viaplay.interview.artistopedia.model.RestExceptionResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * the global exception handler for the Gateway application. Catches exceptions of types
 * {@link RestException} and transforms them
 * into a serializable {@link RestExceptionResponse} for the client to consume.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {


    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(RestException.class)
    public ResponseEntity<RestExceptionResponse> handleRestException(RestException restException) {
        RestExceptionResponse restExceptionResponse;

        restExceptionResponse = RestExceptionResponse.builder().message(restException.getMessage()).status(restException.getHttpStatus()).build();
        return new ResponseEntity<>(restExceptionResponse, restException.getHttpStatus());
    }
}

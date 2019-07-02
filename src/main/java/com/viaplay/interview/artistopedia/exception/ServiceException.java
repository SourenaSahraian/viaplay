package com.viaplay.interview.artistopedia.exception;

import org.springframework.http.HttpStatus;

public enum ServiceException implements IError {


    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not Found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal service error"),
    EXTERNAL_SERVICE_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "request timed out");

    ServiceException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private HttpStatus httpStatus;

    private String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

package com.viaplay.interview.artistopedia.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

public interface IError {

    @JsonIgnore
    HttpStatus getHttpStatus();

    String getMessage();
}

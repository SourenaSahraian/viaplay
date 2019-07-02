package com.viaplay.interview.artistopedia.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestException extends RuntimeException {

   public RestException(IError error){
        this.httpStatus= error.getHttpStatus();
        this.message= error.getMessage();
    }

    private  HttpStatus httpStatus;

    private String message;

}

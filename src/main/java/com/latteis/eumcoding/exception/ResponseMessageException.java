package com.latteis.eumcoding.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ResponseMessageException extends RuntimeException{

    private final ErrorCode errorCode;
    /*private final HttpStatus httpStatus;
    private final String message;*/

}

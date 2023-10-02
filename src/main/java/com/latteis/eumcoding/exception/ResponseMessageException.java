package com.latteis.eumcoding.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseMessageException extends RuntimeException{

    private final ErrorCode errorCode;

}

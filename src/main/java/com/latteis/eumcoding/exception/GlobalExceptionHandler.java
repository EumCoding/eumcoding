package com.latteis.eumcoding.exception;

import com.latteis.eumcoding.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResponseMessageException.class})
    protected ResponseEntity handleResponseMessageException(ResponseMessageException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(new ExceptionDTO(ex.getErrorCode()));
    }
  /*  @ExceptionHandler({ResponseMessageException.class})
    protected ResponseEntity handleResponseMessageException(ResponseMessageException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ExceptionDTO(ex.getMessage())));
    }*/
//
//    @ExceptionHandler({RuntimeException.class})
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String

}

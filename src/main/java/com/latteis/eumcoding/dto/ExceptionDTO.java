package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ExceptionDTO {

    private ErrorCode errorCode;
    private String message;

    public ExceptionDTO(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

}

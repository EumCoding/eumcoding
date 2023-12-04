/*
package com.latteis.eumcoding.exception;

import com.latteis.eumcoding.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ControllerAspect {

    */
/**
     * 컨트롤러 오류 발생 시 오류 메시지를 반환시킵니다.
     *//*

    @Around("within(com.latteis.eumcoding.controller.*)")
    public Object exception(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseDTO.builder().error(e.getMessage()).build());
        }
    }

}*/

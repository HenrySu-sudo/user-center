package org.gatech.usercenter.aop;


import lombok.extern.slf4j.Slf4j;
import org.gatech.usercenter.common.errorCode;
import org.gatech.usercenter.common.generalRespon;
import org.gatech.usercenter.exception.businessException;
import org.gatech.usercenter.utils.resultutils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class globalExceptionHandler {

    @ExceptionHandler(businessException.class)
    public generalRespon<?> businessExceptionHandler(businessException businessException){
        return resultutils.error(businessException.getCode(),businessException.getMessage(),businessException.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public generalRespon<?> runtimeExceptionHandler(RuntimeException runtimeException){
        return  resultutils.error(errorCode.SYSTEM_ERROR.getCode(),runtimeException.getMessage(),"");
    }
}

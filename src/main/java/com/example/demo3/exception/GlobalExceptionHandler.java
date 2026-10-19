package com.example.demo3.exception;

import com.example.demo3.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(exception = BusinessException.class)
    public Result businessExceptionHandler(BusinessException e){
        log.error("业务异常: {}", e.getMessage());
        return Result.fail(e.getMessage());
    }
    @ExceptionHandler(exception = Exception.class)
    public Result exceptionHandler(Exception e){
        log.error("系统异常：", e);
      return Result.fail("后端出现错误，请联系后盾开发同事！");
    }
}

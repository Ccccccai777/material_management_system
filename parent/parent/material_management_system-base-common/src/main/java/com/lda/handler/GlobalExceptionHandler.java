package com.lda.handler;

import com.lda.response.Result;
import com.lda.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result BindExceptionHandler(BindException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return Result.error(6666,message);
    }

    //处理请求参数格式错误 @RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Result ConstraintViolationExceptionHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());
        return Result.error(7859,message);
    }

    //处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常。
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return Result.error(7860,message);
    }
    @ExceptionHandler(BusinessException.class)
    public Result error(BusinessException e){
        System.out.println("Exception111111111111");
        log.error(e.getErrMsg());
        return Result.error(e.getCode(),e.getErrMsg());
    }
    /**
     * 全局异常处理，没有指定异常类型
     * @param  e
     * */
    @ExceptionHandler(Exception.class)
    public Result error(Exception e){
        System.out.println("Exception111111111111");
        //e.printStackTrace();
        log.error(e.getMessage());
        return Result.error(789769,e.getMessage());
    }

    /**
     * shiro的异常
     * @param e
     * @return
     */
    @ExceptionHandler(ShiroException.class)
    public Result handle401(ShiroException e) {
        log.error("shiro异常=>{}",e.getMessage());
        return  Result.error(ResultCode.NO_PERMISSION_EXCEPTION.getCode(),ResultCode.NO_PERMISSION_EXCEPTION.getMessage());
    }

    @ExceptionHandler(ArithmeticException.class)
    public Result error(ArithmeticException e){
        log.error(e.getMessage());
        return Result.error().code(ResultCode.ARITHMETIC_EXCEPTION.getCode()).message(ResultCode.ARITHMETIC_EXCEPTION.getMessage());
    }

}

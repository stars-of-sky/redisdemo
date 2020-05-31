package com.rp.redisdemo.common.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.google.common.collect.ImmutableMap;
import com.rp.redisdemo.common.contants.ExceptionCodeEnum;
import com.rp.redisdemo.common.exception.CustomException;
import com.rp.redisdemo.common.exception.ResponseEntity;
import com.rp.redisdemo.common.exception.ResultCode;
import com.rp.redisdemo.entity.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Create By yrp.
 * Date:2020/5/22
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    // 定义map，存贮常见错误信息。该类map不可修改
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;

    // 构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();

    //将常见的异常存入其中，并定义错误代码。对于其他不常见的异常，即map中没有的，同一一个异常对象返回
    static {
        builder.put(HttpMessageNotReadableException.class, ExceptionCodeEnum.INVALID_PARAM);
        builder.put(MethodArgumentNotValidException.class, ExceptionCodeEnum.INVALID_PARAM);
        builder.put(IllegalArgumentException.class, ExceptionCodeEnum.INVALID_PARAM);
//        builder.put(ResponseStatusException.class, ExceptionCodeEnum.INVALID_PARAM);
    }

    /**
     * 捕获自定义CustomException
     *
     * @param custom
     * @return 结果信息
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseEntity customException(CustomException custom) {
        ResultCode resultCode = custom.getResultCode();
        return new ResponseEntity(resultCode);
    }

    /**
     * 捕获非自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity exception(Exception e) {
        log.error("catch exception {}", e.getMessage());
        if (null == EXCEPTIONS) {
            EXCEPTIONS = builder.build();
        }
        ResultCode resultCode = EXCEPTIONS.get(e.getClass());
        if (resultCode != null) {
            return new ResponseEntity(resultCode);
        } else {
            return new ResponseEntity(ExceptionCodeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * -------- 通用异常处理方法 --------
     **/
//    @ResponseBody
//    @ExceptionHandler
    public Result error(Exception e) {
        //e.printStackTrace();
        log.error(ExceptionUtil.getMessage(e));
        return Result.error();
    }

    /**
     * -------- 指定异常处理方法 --------
     **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.builder().success(false).code(HttpStatus.BAD_REQUEST.value()).message(String.valueOf(errors)).build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.builder().success(false).code(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build();
    }
}

package com.rp.redisdemo.entity.common;

import com.rp.redisdemo.common.contants.ExceptionCodeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * Create By yrp.
 * Date:2020/5/22
 * 统一返回体
 * 外界只可以调用统一返回类的方法，不可以直接创建，因此构造器私有；
 * 内置静态方法，返回对象；
 * 为便于自定义统一结果的信息，建议使用链式编程，将返回对象设类本身，即return this;
 * 响应数据由于为json格式，可定义为JsonObject或Map形式；
 */
@Data
@Accessors(chain = true)
/**
 * @Accessors：
 * 1. fluent 一个布尔值。如果为真，pepper的getter就是 pepper()，setter方法就是pepper(T newValue)。并且，除非特别说明，chain默认为真。
 * 2. chain 一个布尔值。如果为真，产生的setter返回的this而不是void。默认是假。如果fluent=true，那么chain默认为真。
 *
 */
//@Builder
public class Result {
    //响应是否成功
    private Boolean success;
    //状态码
    private Integer code;
    //响应信息
    private String message;
    //响应数据
    private Map<String, Object> data = new HashMap<>();

    //私有构造器
    private Result() {
    }

    /**
     * ---------------连式编程，返回类本身-------------
     **/
    //自定义 返回响应状态
    public Result success(Boolean success) {
        this.success = success;
        return this;
    }

    //自定义状态码
    public Result code(Integer code) {
        this.code = code;
        return this;
    }

    //自定义状态信息
    public Result message(String message) {
        this.message = message;
        return this;
    }

    //返回数据
    public Result date(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    //返回自定义数据
    public Result date(Map<String, Object> data) {
        this.setData(data);
        return this;
    }

    //通用成功
    public static Result ok() {
        //使用 @Accessors(chain = true) 注解
        return new Result().setSuccess(ExceptionCodeEnum.SUCCESS.getSuccess())
                .setCode(ExceptionCodeEnum.SUCCESS.getCode())
                .setMessage(ExceptionCodeEnum.SUCCESS.getMessage());
      /* 使用@Builder  注解
        return Result.builder().build().setSuccess(ExceptionCodeEnum.SUCCESS.getSuccess())
        .setCode(ExceptionCodeEnum.SUCCESS.getCode())
        .setMessage(ExceptionCodeEnum.SUCCESS.getMessage());*/
    }

    //通用失败
    public static Result error() {
        return new Result().setSuccess(ExceptionCodeEnum.UNKNOWN_ERROR.getSuccess())
                .setCode(ExceptionCodeEnum.UNKNOWN_ERROR.getCode())
                .setMessage(ExceptionCodeEnum.UNKNOWN_ERROR.getMessage());
    }
}

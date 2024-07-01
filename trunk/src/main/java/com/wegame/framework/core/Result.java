package com.wegame.framework.core;

import lombok.Getter;

//设置统一资源返回结果集
@Getter
public class Result {
    private final Integer code;
    private final String message;
    private final Object data;

    private Result(ResultCode resultCode, Object data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    private Result(ResultCode resultCode) {
        this(resultCode, null);
    }

    //返回成功的结果集
    public static Result success() {
        return new Result(ResultCode.SUCCESS);
    }

    //返回带参的成功结果集
    public static Result success(Object data) {
        return new Result(ResultCode.SUCCESS, data);
    }

    //返回失败的结果集
    public static Result failure(ResultCode resultCode) {
        return new Result(resultCode);
    }
    public static Result failure(ResultCode resultCode,Object data) {
        return new Result(resultCode,data);
    }

}


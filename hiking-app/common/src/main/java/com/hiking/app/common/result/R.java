package com.hiking.app.common.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class R<T> implements Serializable {

    private int code;
    private String msg;
    private T data;

    private R() {}

    public static <T> R<T> ok() {
        return build(ResultCode.SUCCESS, null);
    }

    public static <T> R<T> ok(T data) {
        return build(ResultCode.SUCCESS, data);
    }

    public static <T> R<T> fail() {
        return build(ResultCode.FAIL, null);
    }

    public static <T> R<T> fail(String msg) {
        R<T> r = new R<>();
        r.setCode(ResultCode.FAIL.getCode());
        r.setMsg(msg);
        return r;
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return build(resultCode, null);
    }

    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> R<T> build(ResultCode resultCode, T data) {
        R<T> r = new R<>();
        r.setCode(resultCode.getCode());
        r.setMsg(resultCode.getMsg());
        r.setData(data);
        return r;
    }
}
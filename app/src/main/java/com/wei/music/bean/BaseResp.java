package com.wei.music.bean;

public class BaseResp<T> {

    protected int code;
    protected String message;
    protected T data;

    public int getCode() {
        return code;
    }

    public BaseResp<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BaseResp<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public BaseResp<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean success() {
        return code >= 200 && code < 299;
    }
}

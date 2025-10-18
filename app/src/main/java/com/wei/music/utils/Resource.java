package com.wei.music.utils;

public abstract class Resource<T> {

    private final T data;
    private final String message;
    private final Throwable throwable;
    private final Integer statusCode;

    protected Resource(T data, String message, Throwable throwable, Integer statusCode) {
        this.data = data;
        this.message = message;
        this.throwable = throwable;
        this.statusCode = statusCode;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public boolean isSuccess() {
        return this instanceof Success;
    }

    public boolean isLoading() {
        return this instanceof Loading;
    }

    public boolean isError() {
        return this instanceof Error;
    }

    public boolean isEmpty() {
        return this instanceof Empty;
    }

    // Success 子类
    public static class Success<T> extends Resource<T> {
        public Success(T data) {
            super(data, null, null, null);
        }

        public Success(T data, Integer statusCode) {
            super(data, null, null, statusCode);
        }
    }

    // Error 子类
    public static class Error<T> extends Resource<T> {
        public Error(String message) {
            super(null, message, null, null);
        }

        public Error(String message, Throwable throwable) {
            super(null, message, throwable, null);
        }

        public Error(String message, Throwable throwable, Integer statusCode) {
            super(null, message, throwable, statusCode);
        }

        public Error(String message, Throwable throwable, Integer statusCode, T data) {
            super(data, message, throwable, statusCode);
        }
    }

    // Loading 子类
    public static class Loading<T> extends Resource<T> {
        public Loading() {
            super(null, null, null, null);
        }

        public Loading(T data) {
            super(data, null, null, null);
        }
    }

    // Empty 子类
    public static class Empty<T> extends Resource<T> {
        public Empty() {
            super(null, null, null, null);
        }
    }
}
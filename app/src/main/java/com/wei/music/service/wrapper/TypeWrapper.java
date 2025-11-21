package com.wei.music.service.wrapper;

public class TypeWrapper<T> {

    private T data;

    private TypeWrapper(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Local<T> local(T data) {
        return new Local<>(data);
    }

    public static <T> Remote<T> remote(T data) {
        return new Remote<>(data);
    }

    public static <T> Mixed<T> mixed(T data) {
        return new Mixed<>(data);
    }

    public static class Local<T> extends TypeWrapper<T> {

        private Local(T data) {
            super(data);
        }
    }

    public static class Remote<T> extends TypeWrapper<T> {

        private Remote(T data) {
            super(data);
        }
    }

    public static class Mixed<T> extends TypeWrapper<T> {

        private Mixed(T data) {
            super(data);
        }
    }
}

package com.wei.music.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CacheInterceptor implements Interceptor {

    private Context cont;
    private int age;
    
    public CacheInterceptor(Context cont, int age) {
        this.cont = cont;
        this.age = age;
    }

    @Override
    public Response intercept(@NonNull Chain chain) {
        Request request = chain.request();

        try {
            if (ToolUtil.haveNetwork(cont)) {
                Response response = chain.proceed(request);
                String cacheControl = new CacheControl.Builder()
                        .maxAge(age, TimeUnit.SECONDS)
                        .build()
                        .toString();
                return response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", cacheControl)
                        .build();
            } else {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                return chain.proceed(request);
            }
        } catch (IOException e) {
            e.printStackTrace();

            // 创建自定义错误响应
            return createErrorResponse(request, 504, "Network Error: " + e.getMessage());
        }
    }

    private Response createErrorResponse(Request request, int code, String message) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(message)
                .body(ResponseBody.create(null, ""))
                .build();
    }
}

package com.wei.music.network;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Random;

import jakarta.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DefaultInterceptor implements Interceptor {
    @Inject
    public DefaultInterceptor() {
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();

        // 添加完整的浏览器头信息
        builder.header("User-Agent", getRandomUserAgent())
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .header("Referer", "https://netstart.cn/")
                .header("Origin", "https://netstart.cn")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-origin");

        return chain.proceed(builder.build());
    }

    private static String getRandomUserAgent() {
        String[] userAgents = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
        };
        return userAgents[new Random().nextInt(userAgents.length)];
    }
}
package com.wei.music.network;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;


public class PersistentCookieJar implements CookieJar {
    private final Map<HttpUrl, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    @Inject
    public PersistentCookieJar() {
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        cookieStore.put(url, cookies);
        // 这里可以添加持久化到 SharedPreferences 的逻辑
    }

    @NonNull
    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies != null ? cookies : new ArrayList<>();
    }
}
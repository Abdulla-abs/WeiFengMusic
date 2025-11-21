package com.wei.music.network;

import androidx.annotation.NonNull;

import com.wei.music.AppSessionManager;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.utils.Resource;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import dagger.Lazy;
import jakarta.inject.Inject;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CookieInterceptor implements Interceptor {
    private final Lazy<AppSessionManager> sessionManagerLazy;

    @Inject
    public CookieInterceptor(Lazy<AppSessionManager> sessionManagerLazy) {
        this.sessionManagerLazy = sessionManagerLazy;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final String url = chain.request().url().toString();
        AppSessionManager appSessionManager = sessionManagerLazy.get();

        Resource<UserLoginBean> value = Optional.ofNullable(appSessionManager)
                .map(new Function<AppSessionManager, Resource<UserLoginBean>>() {
                    @Override
                    public Resource<UserLoginBean> apply(AppSessionManager appSessionManager) {
                        return appSessionManager.userLoginLiveData.getValue();
                    }
                })
                .orElse(new Resource.Empty<>());

        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();

        // 如果不是登录接口，添加 cookie
        if (!url.contains("login") && value instanceof Resource.Success) {
            String cookie = ((Resource.Success<UserLoginBean>) value).getData().getCookie();

            if (cookie != null && !cookie.trim().isEmpty()) {
                //String encodedCookie = URLEncoder.encode(cookie, "UTF-8");

                if (originalRequest.method().equalsIgnoreCase("GET")) {
                    // GET 方法：在 URL 后添加 cookie 参数
                    HttpUrl newUrl = originalRequest.url().newBuilder()
                            //.addQueryParameter("cookie", encodedCookie)
                            .build();
                    requestBuilder.url(newUrl);

                } else if (originalRequest.method().equalsIgnoreCase("POST")) {
                    // POST 方法：检查请求体类型
                    RequestBody originalBody = originalRequest.body();

                    if (originalBody instanceof FormBody) {
                        // 表单类型的 POST 请求
                        FormBody.Builder formBodyBuilder = new FormBody.Builder();
                        FormBody originalFormBody = (FormBody) originalBody;

                        // 复制原有参数
                        for (int i = 0; i < originalFormBody.size(); i++) {
                            formBodyBuilder.add(originalFormBody.name(i), originalFormBody.value(i));
                        }

                        // 添加 cookie 参数
                        formBodyBuilder.add("cookie", cookie);

                        requestBuilder.method(originalRequest.method(), formBodyBuilder.build());

                    } else if (originalBody instanceof MultipartBody) {
                        // 文件上传等 Multipart 请求，在表单部分添加 cookie
                        // 这里需要更复杂的处理，通常建议在 Header 中添加
                        requestBuilder.addHeader("Cookie", cookie);

                    } else {
                        // 其他类型的 POST 请求（如 JSON），建议在 Header 中添加
                        requestBuilder.addHeader("Cookie", cookie);
                    }
                }
            }
        }

        Request newRequest = requestBuilder.build();
        return chain.proceed(newRequest);
    }
}


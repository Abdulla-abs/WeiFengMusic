package com.wei.music.network;

import androidx.annotation.NonNull;

import com.wei.music.AppSessionManager;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.utils.Resource;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    private static final String LOGGING_URL = "https://apis.netstart.cn/music/";
    private static final String NESTED_BASE_URL = "https://163api.qijieya.cn/";
    public static final String WEI_BASE_URL = "https://netease-cloud-music-api-wei.vercel.app/";

    private final LoginApi loginApi;
    private final NestedApi nestedApi;
    private final WeiApi weiApi;

    private ApiService() {
        nestedApi = new Retrofit.Builder()
                .baseUrl(NESTED_BASE_URL)
                .client(getDefOkhttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(NestedApi.class);

        weiApi = new Retrofit.Builder()
                .baseUrl(WEI_BASE_URL)
                .client(getDefOkhttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(WeiApi.class);

        loginApi = new Retrofit.Builder()
                .baseUrl(LOGGING_URL)
                .client(getDefOkhttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(LoginApi.class);
    }

    public NestedApi getNestedApi() {
        return nestedApi;
    }

    public WeiApi getWeiApi() {
        return weiApi;
    }

    public LoginApi getLoginApi() {
        return loginApi;
    }

    public static class ServiceHolder {
        public static ApiService service = new ApiService();
    }

    private OkHttpClient getDefOkhttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new DefaultInterceptor())
                .addInterceptor(getLoggingInterceptor())
                .addInterceptor(new CookieInterceptor())
                .cookieJar(getCookieJar())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    private CookieJar getCookieJar() {
        return new PersistentCookieJar();
    }

    private static class DefaultInterceptor implements Interceptor {

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

    private static class CookieInterceptor implements Interceptor {

        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            final String url = chain.request().url().toString();
            Resource<UserLoginBean> value = AppSessionManager.Holder.instance.userLoginLiveData.getValue();

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
}

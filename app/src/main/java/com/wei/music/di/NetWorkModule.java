package com.wei.music.di;

import com.wei.music.network.CookieInterceptor;
import com.wei.music.network.DefaultInterceptor;
import com.wei.music.network.LoginApi;
import com.wei.music.network.NestedApi;
import com.wei.music.network.PersistentCookieJar;
import com.wei.music.network.WeiApi;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import jakarta.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public abstract class NetWorkModule {

    private static final String LOGGING_URL = "https://apis.netstart.cn/music/";
    private static final String NESTED_BASE_URL = "https://163api.qijieya.cn/";
    public static final String WEI_BASE_URL = "https://netease-cloud-music-api-wei.vercel.app/";

    @Provides
    public static HttpLoggingInterceptor providerDefaultLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    @Provides
    public static OkHttpClient providerDefaultOkhttpClient(DefaultInterceptor defaultInterceptor,
                                                    CookieInterceptor cookieInterceptor,
                                                    HttpLoggingInterceptor httpLoggingInterceptor,
                                                    PersistentCookieJar persistentCookieJar) {
        return new OkHttpClient.Builder()
                .addInterceptor(defaultInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(cookieInterceptor)
                .cookieJar(persistentCookieJar)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

//    @Singleton
    @Provides
    public static LoginApi providerLoginApi(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(LOGGING_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(LoginApi.class);
    }

//    @Singleton
    @Provides
    public static NestedApi providerNestedApi(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(NESTED_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(NestedApi.class);
    }

//    @Singleton
    @Provides
    public static WeiApi providerWeiApi(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(WEI_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(WeiApi.class);
    }
}

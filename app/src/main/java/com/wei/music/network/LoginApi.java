package com.wei.music.network;

import com.wei.music.bean.BaseResp;
import com.wei.music.bean.SubCountBean;
import com.wei.music.bean.UserLoginBean;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LoginApi {

    @GET("login/cellphone")
    Observable<Response<UserLoginBean>> captchaLogin(@Query("phone") String phoneNumber, @Query("captcha") String captcha);

    @GET("login/cellphone")
    Observable<BaseResp<UserLoginBean>> passwordLogin(@Query("phone") String phoneNumber, @Query("password") String password);

    @GET("captcha/sent")
    Observable<BaseResp<Boolean>> requestCaptcha(@Query("phone") String phoneNumber, @Query("ctcode") String ctCode);

    @GET("user/subcount")
    Observable<SubCountBean> getSubCount();

    @GET("/user/playlist")
    Observable<String> getPlayList(@Query("uid") Integer uid);

}

package com.wei.music.network;

import com.wei.music.bean.BaseResp;
import com.wei.music.bean.MusicUrlDTO;
import com.wei.music.bean.SubCountBean;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.bean.UserMusicListBean;
import com.wei.music.bean.UserSubCount;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NestedApi {

    @GET("login/cellphone")
    Observable<Response<UserLoginBean>> captchaLogin(@Query("phone") String phoneNumber, @Query("captcha") String captcha);

    @GET("login/cellphone")
    Observable<BaseResp<UserLoginBean>> passwordLogin(@Query("phone") String phoneNumber, @Query("password") String password);

    @GET("captcha/sent")
    Observable<BaseResp<Boolean>> requestCaptcha(@Query("phone") String phoneNumber, @Query("ctcode") String ctCode);

    @GET("user/subcount")
    Observable<SubCountBean> getSubCount();

    @GET("/user/playlist")
    Observable<Response<UserSubCount>> getPlayList(@Query("uid") Integer uid);

    @GET("/playlist/detail")
    Single<UserMusicListBean> getSongListDetail(@Query("id") Long songGroupId);

    @GET("/song/url")
    Single<Response<MusicUrlDTO>> getMusicUrl(@Query("id")Long musicId);

    @GET("/like")
    Single<BaseResp<String>> likeMusic(boolean like, int id);
}

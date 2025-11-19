package com.wei.music.network;

import com.wei.music.bean.BaseResp;
import com.wei.music.bean.UserMusicListBean;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeiApi {

    @GET("/playlist/detail")
    Single<UserMusicListBean> getSongListDetail(@Query("id") Long songGroupId);

    @GET("/like")
    Single<BaseResp<String>> likeMusic(@Query("like") Boolean like, @Query("id") Integer musicId);

}

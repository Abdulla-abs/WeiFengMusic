package com.wei.music.network;

import com.wei.music.bean.BaseResp;
import com.wei.music.bean.MusicUrlDTO;
import com.wei.music.bean.SearchResultDTO;
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
    Single<Response<MusicUrlDTO>> getMusicUrl(@Query("id") Long musicId);

    @GET("/like")
    Single<BaseResp<Object>> likeMusic(@Query("like")Boolean like,@Query("id") int id);

    /**
     * @param keywords keywords : 关键词
     * @param limit 返回数量 , 默认为 30
     * @param offset 偏移数量，用于分页 , 如 : 如 :( 页数 -1)*30, 其中 30 为 limit 的值 , 默认为 0
     * @param type 默认为 1 即单曲 , 取值意义 : 1: 单曲, 10: 专辑, 100: 歌手, 1000: 歌单, 1002: 用户, 1004: MV,
     *             1006: 歌词, 1009: 电台, 1014: 视频, 1018:综合, 2000:声音(搜索声音返回字段格式会不一样)
     */
    @GET("/search")
    Single<SearchResultDTO> searchMusic(@Query("keywords") String keywords, @Query("limit") Integer limit,
                                        @Query("offset") Integer offset,
                                        @Query("type") Integer type);
}

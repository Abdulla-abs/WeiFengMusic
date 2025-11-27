package com.wei.music.service.controller;

import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.bean.BaseResp;
import com.wei.music.bean.MusicUrlDTO;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.UserMusicListBean;
import com.wei.music.bean.UserSubCount;
import com.wei.music.network.NestedApi;
import com.wei.music.service.MusicService;
import com.wei.music.service.wrapper.TypeWrapper;
import com.wei.music.utils.Resource;
import com.wei.music.utils.RxSchedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Response;

@Singleton
public class RemoteSongDataSource extends MusicDataSource {

    private final NestedApi nestedApi;

    @Inject
    public RemoteSongDataSource(NestedApi nestedApi) {
        this.nestedApi = nestedApi;
    }

    @Override
    public Single<List<PlaylistDTO>> fetchSongList(Integer userId) {
        if (userId == null) return Single.just(Collections.emptyList());
        return nestedApi
                .getPlayList(userId)
                .compose(RxSchedulers.applySchedulers())
                .map(new Function<Response<UserSubCount>, List<PlaylistDTO>>() {
                    @Override
                    public List<PlaylistDTO> apply(Response<UserSubCount> userSubCountResponse) throws Throwable {
                        if (userSubCountResponse.isSuccessful() && userSubCountResponse.body() != null) {
                            UserSubCount userSubCount = userSubCountResponse.body();
                            return userSubCount.getPlaylist();
                        }
                        return Collections.emptyList();
                    }
                })
                .first(Collections.emptyList());
    }

    /**
     *
     * @param songList
     * @return
     */
    @Override
    public Single<TypeWrapper<UserMusicListBean.PlayList>> fetchSongListDetail(PlaylistDTO songList) {
        return nestedApi.getSongListDetail(songList.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<UserMusicListBean, TypeWrapper<UserMusicListBean.PlayList>>() {
                    @Override
                    public TypeWrapper<UserMusicListBean.PlayList> apply(UserMusicListBean userMusicListBean) throws Throwable {
                        return TypeWrapper.remote(userMusicListBean.getPlaylist());
                    }
                })
                .onErrorReturn(new Function<Throwable, TypeWrapper<UserMusicListBean.PlayList>>() {
                    @Override
                    public TypeWrapper<UserMusicListBean.PlayList> apply(Throwable throwable) throws Throwable {
                        return TypeWrapper.remote(new UserMusicListBean.PlayList());
                    }
                });
    }

    @Override
    public Single<Boolean> changeMusicLikeState(boolean like, int id) {
        return nestedApi.likeMusic(like, id).subscribeOn(Schedulers.io())
                .map(new Function<BaseResp<Object>, Boolean>() {
                    @Override
                    public Boolean apply(BaseResp<Object> objectBaseResp) throws Throwable {
                        return objectBaseResp.success();
                    }
                });
    }

    @Override
    public Single<Resource<MusicUrlDTO.DataDTO>> getMusicUrl(Long musicId) {
        return nestedApi
                .getMusicUrl(musicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Response<MusicUrlDTO>, Resource<MusicUrlDTO.DataDTO>>() {
                    @Override
                    public Resource<MusicUrlDTO.DataDTO> apply(Response<MusicUrlDTO> musicUrlDTOResponse) throws Throwable {
                        if (musicUrlDTOResponse.isSuccessful()) {
                            MusicUrlDTO body = musicUrlDTOResponse.body();
                            if (body != null && body.getCode() == 200) {
                                List<MusicUrlDTO.DataDTO> data = body.getData();
                                if (data.isEmpty()) return new Resource.Error<>("无音质的歌曲");
                                return new Resource.Success<>(data.get(0));
                            } else {
                                return new Resource.Error<>("发生错误。错误码：" + body.getCode());
                            }
                        } else {
                            return new Resource.Error<>("错误：" + musicUrlDTOResponse.message());
                        }
                    }
                });
    }
}

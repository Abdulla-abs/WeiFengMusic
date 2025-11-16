package com.wei.music.service.controller;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.bean.BaseResp;
import com.wei.music.bean.SongListBean;
import com.wei.music.bean.UserMusicListBean;
import com.wei.music.network.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RemoteSongDataSource extends MusicDataSource {
    @Override
    public Single<List<MediaSessionCompat.QueueItem>> resetMusicSet(SongListBean songList) {
        return ApiService.ServiceHolder.service
                .getWeiApi()
                .getSongListDetail(songList.getId())
                .subscribeOn(Schedulers.io())
                .onErrorReturn(new Function<Throwable, UserMusicListBean>() {
                    @Override
                    public UserMusicListBean apply(Throwable throwable) throws Throwable {
                        return new UserMusicListBean();
                    }
                })
                .map(new Function<UserMusicListBean, List<MediaSessionCompat.QueueItem>>() {
                    @Override
                    public List<MediaSessionCompat.QueueItem> apply(UserMusicListBean userMusicListBean) throws Throwable {
                        UserMusicListBean.PlayList playList = Optional.ofNullable(userMusicListBean.playlist)
                                .orElse(new UserMusicListBean.PlayList());
                        List<MediaSessionCompat.QueueItem> list = new ArrayList<>();
                        for (int i = 0; i < playList.tracks.size(); i++) {
                            list.add(new MediaSessionCompat.QueueItem(new MediaMetadataCompat.Builder()
                                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playList.tracks.get(i).name)//歌名
                                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, playList.tracks.get(i).ar.get(0).name)//歌手
                                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, playList.tracks.get(i).al.picUrl)//歌曲封面
                                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, playList.tracks.get(i).id)//歌曲id
                                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, ApiService.WEI_BASE_URL + playList.tracks.get(i).id)
                                    .build().getDescription(), i));
                        }
                        return list;
                    }
                });
    }

    @Override
    public Single<Boolean> changeMusicLikeState(boolean like,int id){
        return ApiService.ServiceHolder.service
                .getWeiApi()
                .likeMusic(like, id)
                .subscribeOn(Schedulers.io())
                .map(new Function<BaseResp<String>, Boolean>() {
                    @Override
                    public Boolean apply(BaseResp<String> stringBaseResp) throws Throwable {
                        if (stringBaseResp.success() && stringBaseResp.getData().contains("200")){
                            return Boolean.TRUE;
                        }else {
                            return Boolean.FALSE;
                        }
                    }
                });
    }
}

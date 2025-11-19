package com.wei.music.service.controller;

import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.bean.BaseResp;
import com.wei.music.bean.MusicUrlDTO;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.UserMusicListBean;
import com.wei.music.network.ApiService;
import com.wei.music.service.MusicService;
import com.wei.music.utils.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Response;

public class RemoteSongDataSource extends MusicDataSource {
    @Override
    public Single<List<MediaSessionCompat.QueueItem>> resetMusicSet(PlaylistDTO songList) {
        return ApiService.ServiceHolder.service
                .getNestedApi()
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
                            MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playList.tracks.get(i).name)
                                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, playList.tracks.get(i).ar.get(0).name)
                                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, playList.tracks.get(i).al.picUrl)
                                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(playList.tracks.get(i).id))
                                    .build();

                            MediaDescriptionCompat desc = metadata.getDescription();

                            Bundle extras = new Bundle();
                            extras.putInt(MusicService.MSCQIMusicType, SongType.REMOTE.type);  // 关键在这里

                            MediaDescriptionCompat finalDesc = new MediaDescriptionCompat.Builder()
                                    .setMediaId(desc.getMediaId())
                                    .setTitle(desc.getTitle())
                                    .setSubtitle(desc.getSubtitle())
                                    .setIconUri(desc.getIconUri())
                                    .setDescription(desc.getDescription())
                                    .setExtras(extras)  // 手动设置
                                    .build();

                            list.add(new MediaSessionCompat.QueueItem(finalDesc, i));
                        }
                        return list;
                    }
                });
    }

    @Override
    public Single<Boolean> changeMusicLikeState(boolean like, int id) {
        return ApiService.ServiceHolder.service
                .getWeiApi()
                .likeMusic(like, id)
                .subscribeOn(Schedulers.io())
                .map(new Function<BaseResp<String>, Boolean>() {
                    @Override
                    public Boolean apply(BaseResp<String> stringBaseResp) throws Throwable {
                        if (stringBaseResp.success() && stringBaseResp.getData().contains("200")) {
                            return Boolean.TRUE;
                        } else {
                            return Boolean.FALSE;
                        }
                    }
                });
    }

    @Override
    public Single<Resource<MusicUrlDTO.DataDTO>> getMusicUrl(Long musicId) {
        return ApiService.ServiceHolder.service
                .getNestedApi()
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

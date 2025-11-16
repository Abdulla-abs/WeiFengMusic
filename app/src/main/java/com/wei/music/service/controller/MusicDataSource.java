package com.wei.music.service.controller;

import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.bean.SongListBean;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public abstract class MusicDataSource {

    public abstract Single<List<MediaSessionCompat.QueueItem>> resetMusicSet(SongListBean songList);

    public abstract Single<Boolean> changeMusicLikeState(boolean like,int id);

}

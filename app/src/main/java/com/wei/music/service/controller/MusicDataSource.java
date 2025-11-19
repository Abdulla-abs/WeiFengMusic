package com.wei.music.service.controller;

import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.bean.MusicUrlDTO;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.utils.Resource;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public abstract class MusicDataSource {

    public abstract Single<List<MediaSessionCompat.QueueItem>> resetMusicSet(PlaylistDTO playlistDTO);

    public abstract Single<Boolean> changeMusicLikeState(boolean like, int id);

    public abstract Single<Resource<MusicUrlDTO.DataDTO>> getMusicUrl(Long musicId);
}

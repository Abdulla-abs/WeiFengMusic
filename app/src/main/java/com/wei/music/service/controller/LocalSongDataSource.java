package com.wei.music.service.controller;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.bean.SongListBean;
import com.wei.music.utils.AudioFileFetcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class LocalSongDataSource extends MusicDataSource {

    @Override
    public Single<List<MediaSessionCompat.QueueItem>> resetMusicSet(SongListBean songList) {
        if (AudioFileFetcher.cachedLocalSongsList.isEmpty())
            return Single.just(Collections.emptyList());
        List<MediaSessionCompat.QueueItem> musicList = new ArrayList<>();
        for (int i = 0; i < AudioFileFetcher.cachedLocalSongsList.size(); i++) {
            musicList.add(new MediaSessionCompat.QueueItem(
                    new MediaMetadataCompat.Builder()
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, AudioFileFetcher.cachedLocalSongsList.get(i).getName())//歌名
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, AudioFileFetcher.cachedLocalSongsList.get(i).getArtist())//歌手
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, AudioFileFetcher.cachedLocalSongsList.get(i).getAlbum())//歌曲封面
                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, AudioFileFetcher.cachedLocalSongsList.get(i).getId() + "")//歌曲id
                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, AudioFileFetcher.cachedLocalSongsList.get(i).getPath())
                            .build()
                            .getDescription(),
                    i)
            );
        }
        return Single.just(musicList);
    }

    @Override
    public Single<Boolean> changeMusicLikeState(boolean like, int id) {
        return Single.just(Boolean.TRUE);
    }
}

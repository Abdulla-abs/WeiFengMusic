package com.wei.music.service.controller;

import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.AppSessionManager;
import com.wei.music.bean.MusicUrlDTO;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.SongListBean;
import com.wei.music.service.MusicService;
import com.wei.music.utils.AudioFileFetcher;
import com.wei.music.utils.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import io.reactivex.rxjava3.core.Single;

public class LocalSongDataSource extends MusicDataSource {

    @Override
    public Single<List<MediaSessionCompat.QueueItem>> resetMusicSet(PlaylistDTO songList) {

        if (AppSessionManager.Holder.instance.localAudioFiles.isEmpty())
            return Single.just(Collections.emptyList());
        List<MediaSessionCompat.QueueItem> musicList = new ArrayList<>();
        for (int i = 0; i < AppSessionManager.Holder.instance.localAudioFiles.size(); i++) {
            MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, AppSessionManager.Holder.instance.localAudioFiles.get(i).getName())//歌名
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, AppSessionManager.Holder.instance.localAudioFiles.get(i).getArtist())//歌手
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, AppSessionManager.Holder.instance.localAudioFiles.get(i).getAlbum())//歌曲封面
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, AppSessionManager.Holder.instance.localAudioFiles.get(i).getId() + "")//歌曲id
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, AppSessionManager.Holder.instance.localAudioFiles.get(i).getPath())
                    .build();

            MediaDescriptionCompat desc = metadata.getDescription();

            Bundle extras = new Bundle();
            extras.putInt(MusicService.MSCQIMusicType, SongType.LOCAL.type);  // 关键在这里

            MediaDescriptionCompat finalDesc = new MediaDescriptionCompat.Builder()
                    .setMediaId(desc.getMediaId())
                    .setTitle(desc.getTitle())
                    .setSubtitle(desc.getSubtitle())
                    .setIconUri(desc.getIconUri())
                    .setDescription(desc.getDescription())
                    .setExtras(extras)  // 手动设置
                    .build();

            musicList.add(new MediaSessionCompat.QueueItem(finalDesc, i));
        }
        return Single.just(musicList);
    }

    @Override
    public Single<Boolean> changeMusicLikeState(boolean like, int id) {
        return Single.just(Boolean.TRUE);
    }

    @Override
    public Single<Resource<MusicUrlDTO.DataDTO>> getMusicUrl(Long musicId) {
        if (AppSessionManager.Holder.instance.localAudioFiles == null) {
            return Single.never();
        }
        Optional<AudioFileFetcher.AudioFile> first = AppSessionManager.Holder.instance.localAudioFiles
                .parallelStream()
                .filter(new Predicate<AudioFileFetcher.AudioFile>() {
                    @Override
                    public boolean test(AudioFileFetcher.AudioFile audioFile) {
                        return audioFile.getId() == musicId;
                    }
                })
                .findFirst();
        if (first.isPresent()) {
            MusicUrlDTO.DataDTO dataDTO = new MusicUrlDTO.DataDTO();
            dataDTO.setUrl(first.get().getPath());
            return Single.just(new Resource.Success<>(dataDTO));
        }
        return Single.just(new Resource.Error<>("播放音乐失败：未找到音乐数据"));
    }
}

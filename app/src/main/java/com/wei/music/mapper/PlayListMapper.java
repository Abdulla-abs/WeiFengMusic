package com.wei.music.mapper;

import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.bean.UserMusicListBean;
import com.wei.music.service.MusicService;
import com.wei.music.service.controller.SongType;
import com.wei.music.service.wrapper.TypeWrapper;

import java.util.ArrayList;
import java.util.List;

public class PlayListMapper {

    public static List<MediaSessionCompat.QueueItem> mapper(TypeWrapper<UserMusicListBean.PlayList> playList) {
        List<UserMusicListBean.PlayList.Tracks> trackList = playList.getData().getTracks();
        List<MediaSessionCompat.QueueItem> list = new ArrayList<>();
        for (int i = 0; i < trackList.size(); i++) {
            MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, trackList.get(i).getName())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, trackList.get(i).getAr().get(0).getName())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, trackList.get(i).getAl().getPicUrl())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(trackList.get(i).getId()))
                    .build();

            MediaDescriptionCompat desc = metadata.getDescription();

            Bundle extras = new Bundle();
            if (playList instanceof TypeWrapper.Local) {
                extras.putInt(MusicService.MSCQIMusicType, SongType.LOCAL.type);  // 关键在这里
            } else if (playList instanceof TypeWrapper.Remote) {
                extras.putInt(MusicService.MSCQIMusicType, SongType.REMOTE.type);  // 关键在这里
            } else if (playList instanceof TypeWrapper.Mixed) {
                //TODO 对于可能后期支持的混合歌单列表
            }

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

}

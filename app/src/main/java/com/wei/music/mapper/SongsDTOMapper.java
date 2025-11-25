package com.wei.music.mapper;

import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.activity.search.SearchSongArtistsFlatmap;
import com.wei.music.bean.SearchResultDTO;
import com.wei.music.service.MusicService;
import com.wei.music.service.controller.SongType;

import java.util.function.Consumer;

public class SongsDTOMapper {

    public static MediaSessionCompat.QueueItem mapper(SearchResultDTO.ResultDTO.SongsDTO songsDTO) {
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songsDTO.getName())
                //.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songsDTO.getAlbum().getPicId())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(songsDTO.getId()));

        SearchSongArtistsFlatmap.flatmap(songsDTO.getArtists())
                .ifPresent(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, s);
                    }
                });

        MediaDescriptionCompat desc = metadataBuilder.build().getDescription();

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

        return new MediaSessionCompat.QueueItem(finalDesc, songsDTO.getId());
    }
}

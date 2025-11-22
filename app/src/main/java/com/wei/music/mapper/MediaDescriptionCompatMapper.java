package com.wei.music.mapper;

import android.support.v4.media.MediaDescriptionCompat;

import java.util.Optional;

public class MediaDescriptionCompatMapper {

    public static MediaMetadataInfo mapper(MediaDescriptionCompat description){
        String defStr = "<unknow>";
        String name = Optional.ofNullable(description.getTitle())
                .map(CharSequence::toString).orElse(defStr);
        String singer = Optional.ofNullable(description.getSubtitle())
                .map(CharSequence::toString)
                .orElse(defStr);
        String icon = Optional.ofNullable(description.getDescription())
                .map(CharSequence::toString)
                .orElse(defStr);
        String id = Optional.ofNullable(description.getMediaId())
                .orElse(defStr);
        return new MediaMetadataInfo()
                .setTitle(name)
                .setArtist(singer)
                .setAlbum(icon)
                .setMediaId(id);
    }
}

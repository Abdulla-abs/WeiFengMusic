package com.wei.music.mapper;

import android.support.v4.media.MediaMetadataCompat;

public class MediaMetadataMapper {

    public static MediaMetadataInfo mapper(MediaMetadataCompat metadata) {
        if (metadata == null) return null;
        String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
        String artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
        String album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
        String mediaId = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
        long duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);

        return new MediaMetadataInfo()
                .setTitle(title)
                .setArtist(artist)
                .setAlbum(album)
                .setMediaId(mediaId)
                .setDuration(duration);
    }



}

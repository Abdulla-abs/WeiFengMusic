package com.wei.music.service;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

public class CurrentMusicSnapshot {

    private static CurrentMusicSnapshot instance;

    public static CurrentMusicSnapshot getInstance() {
        if (instance == null) {
            synchronized (CurrentMusicSnapshot.class) {
                if (instance == null) {
                    instance = new CurrentMusicSnapshot();
                }
            }
        }
        return instance;
    }


    private String musicName;
    private String artist;
    private String album;
    private long musicId;
    private int duration;
    private String url;

    private int musicType;
    private int progress;

    private CurrentMusicSnapshot() {
    }

    public long getMusicId() {
        return musicId;
    }

    public CurrentMusicSnapshot setMusicId(long musicId) {
        this.musicId = musicId;
        return this;
    }

    public CurrentMusicSnapshot setMusicId(String musicId) {
        try {
            this.musicId = Long.parseLong(musicId);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public String getMusicName() {
        return musicName;
    }

    public CurrentMusicSnapshot setMusicName(String musicName) {
        this.musicName = musicName;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public CurrentMusicSnapshot setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getAlbum() {
        return album;
    }

    public CurrentMusicSnapshot setAlbum(String album) {
        this.album = album;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public CurrentMusicSnapshot setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public CurrentMusicSnapshot setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public CurrentMusicSnapshot setUrl(String url) {
        this.url = url;
        return this;
    }

    public MediaDescriptionCompat restoreMediaDescription() {
        return new MediaDescriptionCompat.Builder()
                .setTitle(musicName)
                .setSubtitle(artist)
                .setMediaId(String.valueOf(musicId))
                .setMediaUri(Uri.parse(url))
                .build();
    }

    public MediaSessionCompat.QueueItem restoreQueueItem() {
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicName)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(musicId))
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, url)
                .build();
        MediaDescriptionCompat desc = metadata.getDescription();
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.MSCQIMusicType, musicType);
        MediaDescriptionCompat finalDesc = new MediaDescriptionCompat.Builder()
                .setMediaId(desc.getMediaId())
                .setTitle(desc.getTitle())
                .setSubtitle(desc.getSubtitle())
                .setIconUri(desc.getIconUri())
                .setDescription(desc.getDescription())
                .setExtras(bundle)  // 手动设置
                .build();
        return new MediaSessionCompat.QueueItem(finalDesc, 0);
    }

    public void setMusicType(int musicType) {
        this.musicType = musicType;
    }

    public int getMusicType() {
        return musicType;
    }
}

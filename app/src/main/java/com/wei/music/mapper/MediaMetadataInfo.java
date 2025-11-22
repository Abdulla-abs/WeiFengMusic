package com.wei.music.mapper;

public class MediaMetadataInfo {
    private String title;
    private String artist;
    private String album;
    private String mediaId;
    private long duration;

    public String getTitle() {
        return title;
    }

    public MediaMetadataInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public MediaMetadataInfo setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getAlbum() {
        return album;
    }

    public MediaMetadataInfo setAlbum(String album) {
        this.album = album;
        return this;
    }

    public String getMediaId() {
        return mediaId;
    }

    public MediaMetadataInfo setMediaId(String mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public MediaMetadataInfo setDuration(long duration) {
        this.duration = duration;
        return this;
    }
}

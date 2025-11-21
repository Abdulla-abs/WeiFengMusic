package com.wei.music.service.conbine;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.List;

public class MusicState {

    private List<MediaSessionCompat.QueueItem> queue;
    private MediaSessionCompat.QueueItem current;

    public MusicState(List<MediaSessionCompat.QueueItem> queue, MediaSessionCompat.QueueItem current) {
        this.queue = queue;
        this.current = current;
    }

    public List<MediaSessionCompat.QueueItem> getQueue() {
        return queue;
    }

    public MediaSessionCompat.QueueItem getCurrent() {
        return current;
    }
}

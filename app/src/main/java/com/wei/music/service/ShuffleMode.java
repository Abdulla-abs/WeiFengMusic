package com.wei.music.service;

import android.support.v4.media.session.PlaybackStateCompat;

public enum ShuffleMode {
    OFF(PlaybackStateCompat.SHUFFLE_MODE_NONE),
    ON(PlaybackStateCompat.SHUFFLE_MODE_ALL)
    ;
    public final int value;

    ShuffleMode(int value) {
        this.value = value;
    }
}

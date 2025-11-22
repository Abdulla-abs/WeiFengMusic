package com.wei.music.service;

import android.support.v4.media.session.PlaybackStateCompat;

public enum RepeatMode {
    OFF(PlaybackStateCompat.REPEAT_MODE_NONE),        // 顺序播放，播完结束
    ALL(PlaybackStateCompat.REPEAT_MODE_ALL),         // 列表循环
    ONE(PlaybackStateCompat.REPEAT_MODE_ONE),         // 单曲循环
    SHUFFLE(PlaybackStateCompat.REPEAT_MODE_GROUP);   // 随机播放（用 GROUP 兼容老系统
    public final int value;

    RepeatMode(int value) {
        this.value = value;
    }
}

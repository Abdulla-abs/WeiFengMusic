package com.wei.music.service.util;

import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

public class MediaControllerHelper {
    private final MediaControllerCompat mediaController;

    public MediaControllerHelper(MediaControllerCompat controller) {
        this.mediaController = controller;
    }

    /** 判断当前是否正在播放（包括缓冲中也算正在播放） */
    public boolean isPlaying() {
        PlaybackStateCompat state = mediaController.getPlaybackState();
        if (state == null) return false;

        int playbackState = state.getState();
        return playbackState == PlaybackStateCompat.STATE_PLAYING
                || playbackState == PlaybackStateCompat.STATE_BUFFERING;
    }

    /** 一键切换播放/暂停（最常用的方法） */
    public void togglePlayPause() {
        PlaybackStateCompat state = mediaController.getPlaybackState();
        if (state == null) return;

        if (isPlaying()) {
            // 正在播放 → 暂停
            mediaController.getTransportControls().pause();
        } else {
            // 停止、暂停、错误等状态 → 播放
            int pbState = state.getState();
            if (pbState == PlaybackStateCompat.STATE_PAUSED
                    || pbState == PlaybackStateCompat.STATE_STOPPED
                    || pbState == PlaybackStateCompat.STATE_ERROR) {
                mediaController.getTransportControls().play();
            } else {
                // 其他状态（比如 CONNECTING、SKIPPING_TO_NEXT 等）也尝试播放
                mediaController.getTransportControls().play();
            }
        }
    }

    /** 更简洁的写法（很多人喜欢这样写） */
    public void togglePlayPauseSimple() {
        if (isPlaying()) {
            mediaController.getTransportControls().pause();
        } else {
            mediaController.getTransportControls().play();
        }
    }
}
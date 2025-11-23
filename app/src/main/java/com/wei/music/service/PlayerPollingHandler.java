package com.wei.music.service;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;

/**
 * replace of {@link SeekBarThread}
 */
public class PlayerPollingHandler extends Handler {

    private final int WHAT_LOOP = 0x51;
    private final int LOOP_DELAY = 500;

    private final MediaPlayer player;
    private final PlaybackStateCompat.Builder playbackStateBuilder;
    private final MediaSessionCompat mediaSession;

    public PlayerPollingHandler(@NonNull Looper looper, MediaPlayer player, PlaybackStateCompat.Builder playbackStateBuilder, MediaSessionCompat mediaSession) {
        super(looper);
        this.player = player;
        this.playbackStateBuilder = playbackStateBuilder;
        this.mediaSession = mediaSession;
    }

    public void start() {
        stop();
        sendEmptyMessage(WHAT_LOOP);
    }

    public void stop() {
        removeCallbacksAndMessages(null);
    }

    private void continueLoopDelay() {
        sendEmptyMessageDelayed(WHAT_LOOP, LOOP_DELAY);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case WHAT_LOOP:
                if (player != null && player.isPlaying()) {
                    playbackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition(), 1);
                    mediaSession.setPlaybackState(playbackStateBuilder.build());
                }
                continueLoopDelay();
                break;
        }
    }
}

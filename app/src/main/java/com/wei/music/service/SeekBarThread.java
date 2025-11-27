package com.wei.music.service;

import android.media.MediaPlayer;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

/**
 * 由 {@link MusicService} 剥离出来的歌曲状态轮询
 */
@Deprecated
public class SeekBarThread extends Thread{

    private final MediaPlayer player;
    private final PlaybackStateCompat.Builder playbackStateBuilder;
    private final MediaSessionCompat mediaSession;

    public SeekBarThread(MediaPlayer player, PlaybackStateCompat.Builder playbackStateBuilder, MediaSessionCompat mediaSession) {
        this.player = player;
        this.playbackStateBuilder = playbackStateBuilder;
        this.mediaSession = mediaSession;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (player != null && player.isPlaying()) {
                playbackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition(), 1);
                mediaSession.setPlaybackState(playbackStateBuilder.build());
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }
}

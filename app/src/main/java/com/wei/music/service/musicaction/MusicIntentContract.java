package com.wei.music.service.musicaction;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.List;

public interface MusicIntentContract {

    public class ChangePlayListOrSkipToPosition implements MusicIntentContract{
        private final List<MediaSessionCompat.QueueItem> replace;
        private final int startIndex;
        private final PlaybackStateCompat playbackState;

        public ChangePlayListOrSkipToPosition(List<MediaSessionCompat.QueueItem> replace, int startIndex, PlaybackStateCompat playbackState) {
            this.replace = replace;
            this.startIndex = startIndex;
            this.playbackState = playbackState;
        }

        public List<MediaSessionCompat.QueueItem> getReplace() {
            return replace;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public PlaybackStateCompat getPlaybackState() {
            return playbackState;
        }
    }

    class InsertMusicAndPlay implements MusicIntentContract{
        private final MediaSessionCompat.QueueItem insert;

        public InsertMusicAndPlay(MediaSessionCompat.QueueItem insert) {
            this.insert = insert;
        }

        public MediaSessionCompat.QueueItem getInsert() {
            return insert;
        }

    }
}

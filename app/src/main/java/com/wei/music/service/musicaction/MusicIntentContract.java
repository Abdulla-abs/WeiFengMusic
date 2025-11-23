package com.wei.music.service.musicaction;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.List;

public interface MusicIntentContract {

    public class ChangePlayListOrSkipToPosition implements MusicIntentContract{
        private final List<MediaSessionCompat.QueueItem> replace;
        private final int startIndex;

        public ChangePlayListOrSkipToPosition(List<MediaSessionCompat.QueueItem> replace, int startIndex) {
            this.replace = replace;
            this.startIndex = startIndex;
        }

        public List<MediaSessionCompat.QueueItem> getReplace() {
            return replace;
        }

        public int getStartIndex() {
            return startIndex;
        }
    }
}

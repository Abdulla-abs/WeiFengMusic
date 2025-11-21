package com.wei.music.service.musicaction;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.List;

public interface MusicActionContract {

    class OnPlayListClick implements MusicActionContract{
        private final List<MediaSessionCompat.QueueItem> replace;
        private final int startIndex;

        public OnPlayListClick(List<MediaSessionCompat.QueueItem> replace, int startIndex) {
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

    class ChangePlayQueue implements MusicActionContract {
        private final List<MediaSessionCompat.QueueItem> replace;
        private final int startIndex;

        public ChangePlayQueue(List<MediaSessionCompat.QueueItem> replace, int startIndex) {
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

    class Previous implements MusicActionContract {

    }

    class Next implements MusicActionContract {

    }

    class SkipTo implements MusicActionContract {

    }
}

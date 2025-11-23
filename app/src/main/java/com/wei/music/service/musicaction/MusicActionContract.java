package com.wei.music.service.musicaction;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.List;

public interface MusicActionContract {

    class OnSkipToPosition implements MusicActionContract{
        private final int newPosition;

        public OnSkipToPosition(int newPosition) {
            this.newPosition = newPosition;
        }

        public int getNewPosition() {
            return newPosition;
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

}

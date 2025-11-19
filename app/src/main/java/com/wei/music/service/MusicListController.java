package com.wei.music.service;

import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.utils.CloudMusicApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicListController {

    public boolean isLocalSong = true;
    private List<MediaSessionCompat.QueueItem> mLastMusicList = Collections.emptyList();


    public boolean listEmpty() {
        return mLastMusicList.isEmpty();
    }

    public int size() {
        return mLastMusicList.size();
    }

    public void clear() {
        mLastMusicList.clear();
    }

    public void setList(List<MediaSessionCompat.QueueItem> mMusicList) {
        mLastMusicList.clear();
        mLastMusicList = mMusicList;
    }

    public MediaSessionCompat.QueueItem get(int pos) {
        return mLastMusicList.get(pos);
    }

//    public String getMusicSource(int pos){
//        return CloudMusicApi.MUSIC_PLAY + mLastMusicList.get(pos).getDescription().getMediaId();
//    }
//    public Uri getMusicSource(int pos){
//        return mLastMusicList.get(pos).getDescription().getMediaUri();
//    }

}

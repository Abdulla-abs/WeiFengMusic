package com.wei.music.service;

import android.support.v4.media.session.MediaSessionCompat;

public interface ServiceCallback {

    void onAudioBecomingNoisy();


    void onActionPreMusic();


    void onActionPlayMusic();


    void onActionNextMusic();


}

package com.wei.music.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class MediaServiceReceiver extends BroadcastReceiver {

    public static final String ACTION_PRE = "prevMusic";
    public static final String ACTION_PLAY = "playMusic";
    public static final String ACTION_NEXT = "nextMusic";

    private ServiceCallback serviceCallback;

    public MediaServiceReceiver() {
    }

    public MediaServiceReceiver(ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        if (intent.getAction() == null) return;
        switch (intent.getAction()) {
            case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                serviceCallback.onAudioBecomingNoisy();
                break;
            case ACTION_PRE:
                serviceCallback.onActionPreMusic();
                break;
            case ACTION_PLAY:
                serviceCallback.onActionPlayMusic();
                break;
            case ACTION_NEXT:
                serviceCallback.onActionNextMusic();
                break;
        }
    }
}

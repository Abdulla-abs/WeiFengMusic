package com.wei.music.service.controller;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.service.MusicService;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class MusicController extends MediaBrowserCompat.ConnectionCallback{

    private final WeakReference<Context> contextWeakReference;

    private final Bundle musicSubscribeRootHint = new Bundle();
    private MediaBrowserCompat mediaBrowserCompat;
    private MediaControllerCompat mediaControllerCompat;


    @Inject
    public MusicController(@ApplicationContext Context context) {
        this.contextWeakReference = new WeakReference<>(context);
        init();
    }

    private void init() {
        Context context = contextWeakReference.get();

        mediaBrowserCompat = new MediaBrowserCompat(context, new ComponentName(context, MusicService.class), this, musicSubscribeRootHint);
        mediaBrowserCompat.connect();
    }

    /*                 Callback for MediaBrowserCompat.ConnectionCallback                  */

    @Override
    public void onConnected() {
        super.onConnected();
        onMediaBrowserConnected();
    }

    @Override
    public void onConnectionFailed() {
        super.onConnectionFailed();
    }

    @Override
    public void onConnectionSuspended() {
        super.onConnectionSuspended();
    }

    private void onMediaBrowserConnected() {
        MediaSessionCompat.Token token = mediaBrowserCompat.getSessionToken();
        mediaControllerCompat = new MediaControllerCompat(contextWeakReference.get(), token);
    }

    public boolean registerControllerCallback(MediaControllerCompat.Callback callback) {
        if (mediaControllerCompat != null && isConnected()) {
            mediaControllerCompat.registerCallback(callback);
            return true;
        }
        return false;
    }

    public boolean isConnected() {
        if (mediaBrowserCompat == null) return false;
        return mediaBrowserCompat.isConnected();
    }

    public MediaBrowserCompat getMediaBrowserCompat() {
        return mediaBrowserCompat;
    }

    public MediaControllerCompat getMediaControllerCompat() {
        return mediaControllerCompat;
    }
}

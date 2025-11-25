package com.wei.music.service.util;

import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;

public class DefaultConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

    private final ConnectionCallbackInterface delegate;

    public DefaultConnectionCallback(@NonNull ConnectionCallbackInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onConnected() {
        delegate.onConnected();
    }

    @Override
    public void onConnectionFailed() {
        delegate.onConnectionFailed();
    }

    @Override
    public void onConnectionSuspended() {
        delegate.onConnectionSuspended();
    }
}

package com.wei.music.service.util;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;

public interface ConnectionCallbackInterface {
    /**
     * Invoked after {@link MediaBrowserCompat#connect()} when the request has successfully
     * completed. This can also be called when the service is next running after it crashed or
     * has been killed.
     *
     * @see ServiceConnection#onServiceConnected(ComponentName, IBinder)
     * @see ServiceConnection#onServiceDisconnected(ComponentName)
     */
    void onConnected();
    /**
     * Invoked when a connection to the browser service has been lost.
     * This typically happens when the process hosting the service has crashed or been killed.
     * This does not remove the connection itself -- this binding to the service will remain
     * active, and {@link #onConnected()} will be called when the service is next running.
     *
     * @see ServiceConnection#onServiceDisconnected(ComponentName)
     */
    void onConnectionSuspended();
    /**
     * Invoked when the connection to the media browser service failed.
     * Connection failures can happen when the browser failed to bind to the service,
     * or when it is rejected from the service.
     */
    void onConnectionFailed();


}

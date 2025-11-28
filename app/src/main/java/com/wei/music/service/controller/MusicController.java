package com.wei.music.service.controller;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.wei.music.service.MusicService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class MusicController extends MediaBrowserCompat.ConnectionCallback {

    private static final String TAG = "MusicController";

    private final WeakReference<Context> contextWeakReference;
    private final Bundle musicSubscribeRootHint = new Bundle();

    private MediaBrowserCompat mediaBrowserCompat;
    private MediaControllerCompat mediaControllerCompat;

    // ------------------- 新增：连接状态回调 -------------------
    public interface ConnectionStateListener {
        /** 连接成功，controller 已经可以正常使用 */
        void onConnected(MediaControllerCompat controller);

        /** 连接被挂起（服务被杀等） */
        void onConnectionSuspended();

        /** 连接失败 */
        void onConnectionFailed();
    }

    /** 所有注册进来的监听器（支持多个 Activity/ViewModel 同时监听） */
    private final List<ConnectionStateListener> connectionListeners = new ArrayList<>();

    public void addConnectionListener(ConnectionStateListener listener) {
        if (listener == null || connectionListeners.contains(listener)) return;
        connectionListeners.add(listener);

        // 如果当前已经连接成功，直接回调，避免调用方错过
        if (isConnected() && mediaControllerCompat != null) {
            listener.onConnected(mediaControllerCompat);
        }
    }

    public void removeConnectionListener(ConnectionStateListener listener) {
        connectionListeners.remove(listener);
    }
    // ---------------------------------------------------------

    @Inject
    public MusicController(@ApplicationContext Context context) {
        this.contextWeakReference = new WeakReference<>(context);
        init();
    }

    private void init() {
        Context context = contextWeakReference.get();
        if (context == null) return;

        mediaBrowserCompat = new MediaBrowserCompat(
                context,
                new ComponentName(context, MusicService.class),
                this,
                musicSubscribeRootHint
        );
        mediaBrowserCompat.connect();
    }

    /* ====================== MediaBrowser ConnectionCallback ====================== */

    @Override
    public void onConnected() {
        super.onConnected();
        Log.d(TAG, "MediaBrowser onConnected");
        onMediaBrowserConnected();

        // 通知所有监听器
        for (ConnectionStateListener listener : new ArrayList<>(connectionListeners)) {
            listener.onConnected(mediaControllerCompat);
        }
    }

    @Override
    public void onConnectionSuspended() {
        super.onConnectionSuspended();
        Log.w(TAG, "MediaBrowser onConnectionSuspended");
        mediaControllerCompat = null;
        for (ConnectionStateListener listener : new ArrayList<>(connectionListeners)) {
            listener.onConnectionSuspended();
        }
    }

    @Override
    public void onConnectionFailed() {
        super.onConnectionFailed();
        Log.e(TAG, "MediaBrowser onConnectionFailed");
        for (ConnectionStateListener listener : new ArrayList<>(connectionListeners)) {
            listener.onConnectionFailed();
        }
    }

    private void onMediaBrowserConnected() {
        Context context = contextWeakReference.get();
        if (context == null) return;

        MediaSessionCompat.Token token = mediaBrowserCompat.getSessionToken();
        mediaControllerCompat = new MediaControllerCompat(context, token);
    }

    /* ====================== 对外 API ====================== */

    public boolean isConnected() {
        return mediaBrowserCompat != null && mediaBrowserCompat.isConnected();
    }

    public MediaBrowserCompat getMediaBrowser() {
        return mediaBrowserCompat;
    }

    public MediaControllerCompat getMediaControllerCompat() {
        return mediaControllerCompat;
    }

    /** 安全注册 MediaController 回调（建议只在连接成功后再调用） */
    public void registerControllerCallback(MediaControllerCompat.Callback callback) {
        if (mediaControllerCompat != null && isConnected()) {
            mediaControllerCompat.registerCallback(callback);
        }
        // 如果还没连上，调用方应该通过 addConnectionListener 再注册
    }

    public void unregisterControllerCallback(MediaControllerCompat.Callback callback) {
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(callback);
        }
    }

    /** 方便的工具方法：自动等连接成功后再注册 callback（推荐使用） */
    public void registerWhenConnected(MediaControllerCompat.Callback callback) {
        if (callback == null) return;

        if (isConnected() && mediaControllerCompat != null) {
            mediaControllerCompat.registerCallback(callback);
            return;
        }

        // 还没连上，先监听一次连接成功就注册
        addConnectionListener(new ConnectionStateListener() {
            @Override
            public void onConnected(MediaControllerCompat controller) {
                controller.registerCallback(callback);
                // 用完即删，避免重复注册
                removeConnectionListener(this);
            }

            @Override public void onConnectionSuspended() {}
            @Override public void onConnectionFailed() {}
        });
    }
}
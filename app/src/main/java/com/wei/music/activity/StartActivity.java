package com.wei.music.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import com.wei.music.R;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import java.util.Arrays;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;

import android.content.ComponentName;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.RemoteException;

import com.wei.music.utils.DefaultMediaControllerCompatCallback;
import com.wei.music.utils.MMKVUtils;
import com.wei.music.utils.ToolUtil;

import android.content.pm.PackageManager;

import com.wei.music.service.MusicService;

public class StartActivity extends AppCompatActivity {
    private MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();
            try {
                mMediaController = new MediaControllerCompat(StartActivity.this, token);
            } catch (RemoteException e) {
                try {
                    mMediaController = new MediaControllerCompat(StartActivity.this, token);
                } catch (RemoteException e2) {
                    return;
                }
            }
            mMediaController.registerCallback(new DefaultMediaControllerCompatCallback());
        }
    };
    private MediaBrowserCompat mMediaBrowser;
    private MediaControllerCompat mMediaController;
    private final static int WHAT_START = 1;
    private final int mRequestPermissionCode = 100;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ToolUtil.setStatusBarColor(this, Color.TRANSPARENT, Color.TRANSPARENT, true);

        initHandler();
        initMediaBrowser();
        requestPermission23();
    }

    private void initHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == WHAT_START) {
                    if (MMKVUtils.isFirstRun()) {
                        MMKVUtils.appRun();
                    } else {
                        if (mMediaController != null && MMKVUtils.hasLastSongData()) {
                            Bundle bundle = new Bundle();
                            bundle.putString("id", String.valueOf(MMKVUtils.lastSongListId()));
                            bundle.putString("cookie", MMKVUtils.getUserCookie());
                            mMediaController.getTransportControls()
                                    .sendCustomAction(MusicService.ACTION_START_MUSIC, bundle);
                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                            finish();
                        }
                    }

                }
            }
        };
    }

    private void initMediaBrowser() {
        mMediaBrowser = new MediaBrowserCompat(
                this,
                new ComponentName(this, MusicService.class),
                connectionCallback,
                null
        );
        mMediaBrowser.connect();
    }

    private void requestPermission23() {
        final String[] permissions = new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        // 使用 Stream 过滤出没有授权的权限
        String[] unauthorizedPermissions = Arrays.stream(permissions)
                .filter(permission -> ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED)
                .toArray(String[]::new);


        if (unauthorizedPermissions.length != 0) {
            ActivityCompat.requestPermissions(this, unauthorizedPermissions, mRequestPermissionCode);
        } else {
            handler.sendEmptyMessage(WHAT_START);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestPermissionCode == requestCode) {
            for (int grantResult : grantResults) {
                if (grantResult != RESULT_OK) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
            if (hasPermissionDismiss) {
                showPermissionDialog();
            } else {
                handler.sendEmptyMessage(WHAT_START);
            }
        }
    }

    public void showPermissionDialog() {
        startActivity(new Intent(this, PermissionActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
        connectionCallback = null;
    }
}


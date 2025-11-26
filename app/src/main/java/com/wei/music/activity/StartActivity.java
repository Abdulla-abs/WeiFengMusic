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
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;

import android.content.ComponentName;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.RemoteException;

import com.wei.music.activity.main.MainActivity;
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
            mMediaController = new MediaControllerCompat(StartActivity.this, token);
            mMediaController.registerCallback(new DefaultMediaControllerCompatCallback());
        }
    };
    private MediaBrowserCompat mMediaBrowser;
    private MediaControllerCompat mMediaController;
    private final int mRequestPermissionCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ToolUtil.setStatusBarColor(this, Color.TRANSPARENT, Color.TRANSPARENT, true);

        initMediaBrowser();
        init();
    }

    private void init(){
        if (MMKVUtils.isFirstRun()){
            MMKVUtils.appRun();
            String[] permissions = checkPermissions();
            requestPermission23(permissions);
        }else {
            if (mMediaController != null && MMKVUtils.hasCurrentSongList()) {
                Bundle bundle = new Bundle();
                bundle.putString(MusicService.ACTION_SONG_LIST, MMKVUtils.currentSongDataStr());
                bundle.putString("cookie", MMKVUtils.getUserCookie());
                mMediaController.getTransportControls()
                        .sendCustomAction(MusicService.ACTION_INIT_CURRENT_SONG, bundle);
            }
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
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

    private String[] checkPermissions(){
        final String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }else {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }

        // 使用 Stream 过滤出没有授权的权限
       return Arrays.stream(permissions)
                .filter(permission -> ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED)
                .toArray(String[]::new);
    }

    private void requestPermission23(String[] unauthorizedPermissions) {
        if (unauthorizedPermissions.length != 0) {
            ActivityCompat.requestPermissions(this, unauthorizedPermissions, mRequestPermissionCode);
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
//            if (hasPermissionDismiss) {
//                showPermissionDialog();
//            }
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }

    public void showPermissionDialog() {
        startActivity(new Intent(this, PermissionActivity.class));
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        startActivity(new Intent(StartActivity.this, MainActivity.class));
//        finish();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectionCallback = null;
    }
}


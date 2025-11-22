package com.wei.music;

import android.app.Application;
import android.content.Context;

import com.tencent.mmkv.MMKV;
import com.baidu.mobstat.StatService;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class App extends Application {

    public static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        MMKV.initialize(this);
        StatService.start(this);
    }


}


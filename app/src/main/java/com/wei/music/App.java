package com.wei.music;

import android.app.Application;
import android.content.Context;

import com.tencent.mmkv.MMKV;
import com.baidu.mobstat.StatService;
import com.wei.music.database.AppDatabase;

public class App extends Application {

    private static App sApp;
    private static Context mContext;
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        mContext = getApplicationContext();
        MMKV.initialize(this);
        database = AppDatabase.getInstance(this);

        AppSessionManager.Holder.instance.init();
        StatService.start(this);
    }

    public static App getApp() {
        return sApp;
    }

    public static Context getContext() {
        return mContext;
    }

    public static AppDatabase getDatabase() {
        return database;
    }
}


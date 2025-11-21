package com.wei.music;

import com.tencent.mmkv.MMKV;
import com.baidu.mobstat.StatService;
import com.wei.music.database.AppDatabase;
import com.wei.music.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class App extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        MMKV.initialize(this);
        StatService.start(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder()
                .application(this)
                .build();
    }

}


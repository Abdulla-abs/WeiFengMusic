package com.wei.music.di;

import android.app.Application;
import android.content.Context;

import com.wei.music.App;
import com.wei.music.AppSessionManager;
import com.wei.music.MusicSessionManager;
import com.wei.music.database.AppDatabase;
import com.wei.music.di.annotation.ApplicationContext;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;

@Module
public class AppModule {

    // AppModule.java
    @Provides
    @ApplicationContext
    // 这里用你自己定义的
    Context provideApplicationContext(Application application) {
        return application;
    }

//    @Singleton
//    @Provides
//    public AppSessionManager providerAppSessionManager(){
//        return new AppSessionManager();
//    }
//
//    @Singleton
//    @Provides
//    public MusicSessionManager providerMusicSessionManager(){
//        return new MusicSessionManager()
//    }
}

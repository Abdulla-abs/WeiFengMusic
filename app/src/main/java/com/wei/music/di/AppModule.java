package com.wei.music.di;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

import jakarta.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)  // Application 级别
public class AppModule {

    // 提供 Application 本身（很多地方需要）
    @Provides
    @Singleton
    public static Application provideApplication(@ApplicationContext Context context) {
        return (Application) context.getApplicationContext();
    }

}
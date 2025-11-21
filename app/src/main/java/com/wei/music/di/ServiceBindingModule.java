package com.wei.music.di;

import com.wei.music.di.annotation.MusicServiceScoped;
import com.wei.music.service.MusicService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import jakarta.inject.Singleton;

@Module
public abstract class ServiceBindingModule {

//    @MusicServiceScoped
//    @Singleton
    @ContributesAndroidInjector(modules = {MusicServiceModule.class})
    public abstract MusicService contributesMusicService();
}

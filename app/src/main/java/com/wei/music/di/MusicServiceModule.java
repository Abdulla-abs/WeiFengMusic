package com.wei.music.di;

import android.content.Context;

import com.wei.music.App;
import com.wei.music.di.annotation.ApplicationContext;
import com.wei.music.di.annotation.LocalMusicDataSource;
import com.wei.music.di.annotation.RemoteMusicDataSource;
import com.wei.music.network.NestedApi;
import com.wei.music.service.controller.LocalSongDataSource;
import com.wei.music.service.controller.MusicDataSource;
import com.wei.music.service.controller.RemoteSongDataSource;
import com.wei.music.service.controller.SongType;
import com.wei.music.utils.AudioFileFetcher;

import java.util.function.Function;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;

@Module
public abstract class MusicServiceModule {


}



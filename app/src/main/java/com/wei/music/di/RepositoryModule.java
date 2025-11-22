package com.wei.music.di;

import com.wei.music.di.annotation.LocalMusicDataSource;
import com.wei.music.di.annotation.RemoteMusicDataSource;
import com.wei.music.service.controller.MusicDataSource;
import com.wei.music.service.controller.LocalSongDataSource;
import com.wei.music.service.controller.RemoteSongDataSource;
import com.wei.music.service.controller.SongType;
import com.wei.music.utils.AudioFileFetcher;

import java.util.function.Function;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    @LocalMusicDataSource
    public abstract MusicDataSource bindLocalMusicDataSource(LocalSongDataSource impl);

    @Binds
    @RemoteMusicDataSource
    public abstract MusicDataSource bindRemoteMusicDataSource(RemoteSongDataSource impl);

    @Provides
    public static Function<Long, SongType> provideSongTypeResolver() {
        return id -> id == AudioFileFetcher.LOCAL_SONG_LIST_ID ? SongType.LOCAL : SongType.REMOTE;
    }
}
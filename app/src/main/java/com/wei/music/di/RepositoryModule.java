package com.wei.music.di;

import com.wei.music.di.annotation.LocalMusicDataSource;
import com.wei.music.di.annotation.RemoteMusicDataSource;
import com.wei.music.service.controller.LocalSongDataSource;
import com.wei.music.service.controller.MusicDataSource;
import com.wei.music.service.controller.RemoteSongDataSource;
import com.wei.music.service.controller.SongType;
import com.wei.music.utils.AudioFileFetcher;

import java.util.function.Function;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class RepositoryModule {

    @LocalMusicDataSource
    @Binds
    public abstract MusicDataSource bindsLocalMusicDataSource(LocalSongDataSource localSongDataSource);

    @RemoteMusicDataSource
    @Binds
    public abstract MusicDataSource bindsRemoteDataSource(RemoteSongDataSource remoteMusicDataSource);

    @Provides
    public static Function<Long, SongType> providerSongTypeCase() {
        return new Function<Long, SongType>() {
            @Override
            public SongType apply(Long mlong) {
                if (mlong == AudioFileFetcher.LOCAL_SONG_LIST_ID) {
                    return SongType.LOCAL;
                }
                return SongType.REMOTE;
            }
        };
    }

}

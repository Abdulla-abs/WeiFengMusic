package com.wei.music.repository;

import android.net.Uri;

import com.wei.music.bean.MusicUrlDTO;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.UserMusicListBean;
import com.wei.music.database.dao.UserSubCountDao;
import com.wei.music.di.annotation.LocalMusicDataSource;
import com.wei.music.di.annotation.RemoteMusicDataSource;
import com.wei.music.service.controller.MusicDataSource;
import com.wei.music.service.controller.SongType;
import com.wei.music.service.wrapper.TypeWrapper;
import com.wei.music.utils.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import abbas.fun.myutil.Witch;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class MusicRepository {

    private final MusicDataSource localMusicDataSource;
    private final MusicDataSource remoteMusicDataSource;
    private final UserSubCountDao subCountDao;

    private final Function<Long, SongType> songTypeCase;

    @Inject
    public MusicRepository(@LocalMusicDataSource MusicDataSource localMusicDataSource,
                           @RemoteMusicDataSource MusicDataSource remoteMusicDataSource,
                           UserSubCountDao subCountDao,
                           Function<Long, SongType> songTypeCase) {
        this.localMusicDataSource = localMusicDataSource;
        this.remoteMusicDataSource = remoteMusicDataSource;
        this.subCountDao = subCountDao;
        this.songTypeCase = songTypeCase;
    }

    /**
     * 获取数据库的歌单列表
     * @return
     */
    public @NonNull Observable<List<PlaylistDTO>> loadDatabaseSongList() {
        return subCountDao.getAllPlaylists()
                .switchIfEmpty(Observable.just(Collections.emptyList()))
                .subscribeOn(Schedulers.io()); // 防止空
    }

    /**
     * 执行一次安卓系统的音乐文件查询
     * @return
     */
    public Single<List<PlaylistDTO>> fetchLocalSongList() {
        return localMusicDataSource.fetchSongList(null)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess(subCountDao::insertAll)
                .onErrorReturnItem(Collections.emptyList());
    }

    /**
     * 获取远程音乐歌单
     * @param userId
     * @return
     */
    private Single<List<PlaylistDTO>> fetchRemoteSongList(Integer userId) {
        return remoteMusicDataSource.fetchSongList(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess(subCountDao::insertAll)
                .onErrorReturnItem(Collections.emptyList());
    }

    /**
     * 获取双端歌单列表
     * @param userId
     * @return
     */
    public Single<List<PlaylistDTO>> fetchAllSongList(Integer userId) {
        return Single.zip(
                fetchLocalSongList(),
                fetchRemoteSongList(userId),
                new BiFunction<List<PlaylistDTO>, List<PlaylistDTO>, List<PlaylistDTO>>() {
                    @Override
                    public List<PlaylistDTO> apply(List<PlaylistDTO> playlistDTOS, List<PlaylistDTO> playlistDTOS2) throws Throwable {
                        List<PlaylistDTO> all = new ArrayList<>();
                        all.addAll(playlistDTOS);
                        all.addAll(playlistDTOS2);
                        return all;
                    }
                }
        );
    }

    /**
     * 获取歌单列表详情
     * @param playlistDTO
     * @return
     */
    public Single<TypeWrapper<UserMusicListBean.PlayList>> fetchSongListDetail(PlaylistDTO playlistDTO) {
        MusicDataSource musicDataSource = Witch.<Long, MusicDataSource>of(playlistDTO.getId())
                .mapper(songTypeCase)
                .with(SongType.LOCAL, localMusicDataSource)
                .with(SongType.REMOTE, remoteMusicDataSource)
                .withDefault(localMusicDataSource)
                .witchOne();
        return musicDataSource.fetchSongListDetail(playlistDTO);
    }

    /**
     * 获取歌曲播放路径
     * @param musicType
     * @param mediaId
     * @return
     */
    public @NonNull Single<Resource<Uri>> fetchSongUrl(int musicType, String mediaId) {
        return Witch.<Integer, MusicDataSource>of(musicType)
                .with(SongType.LOCAL.type, localMusicDataSource)
                .with(SongType.REMOTE.type, remoteMusicDataSource)
                .witchOne()
                .getMusicUrl(Long.parseLong(mediaId))
                .map(new io.reactivex.rxjava3.functions.Function<Resource<MusicUrlDTO.DataDTO>, Resource<Uri>>() {
                    @Override
                    public Resource<Uri> apply(Resource<MusicUrlDTO.DataDTO> dataDTOResource) throws Throwable {
                        if (dataDTOResource.isSuccess()) {
                            Uri musicUri = Uri.parse(dataDTOResource.getData().getUrl());
                            return new Resource.Success<>(musicUri);
                        }
                        return new Resource.Error<>(dataDTOResource.getMessage());
                    }
                })
                .onErrorReturn(new io.reactivex.rxjava3.functions.Function<Throwable, Resource<Uri>>() {
                    @Override
                    public Resource<Uri> apply(Throwable throwable) throws Throwable {
                        return new Resource.Error<>(throwable.getMessage());
                    }
                });
    }

    public Single<Boolean> changeMusicLikeState(int musicType, int musicId,boolean like){
        return Witch.<Integer, MusicDataSource>of(musicType)
                .with(SongType.LOCAL.type, localMusicDataSource)
                .with(SongType.REMOTE.type, remoteMusicDataSource)
                .witchOne()
                .changeMusicLikeState(like,musicId);
    }
}

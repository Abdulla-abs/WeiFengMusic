package com.wei.music.service.controller;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wei.music.App;
import com.wei.music.bean.MusicUrlDTO;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.UserMusicListBean;
import com.wei.music.service.wrapper.TypeWrapper;
import com.wei.music.utils.AudioFileFetcher;
import com.wei.music.utils.Resource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.functions.Function;

@Singleton
public class LocalSongDataSource extends MusicDataSource {

    private final WeakReference<Context> application;

    //歌单信息
    private final PlaylistDTO defaultLocalPlayList = new PlaylistDTO();
    //歌单信息带歌曲列表
    private final UserMusicListBean.PlayList defaultLocalPlayListDetail = new UserMusicListBean.PlayList();
    //歌单列表条目
    public final MutableLiveData<List<AudioFileFetcher.AudioFile>> _localAudioFiles = new MutableLiveData<>();
    public final LiveData<List<AudioFileFetcher.AudioFile>> localAudioFiles = _localAudioFiles;

    @Inject
    public LocalSongDataSource(@ApplicationContext Context application) {
        this.application = new WeakReference<>(application);
        defaultLocalPlayList.setName("本地歌曲歌单");
        defaultLocalPlayList.setDescription("本地歌曲歌单");
        defaultLocalPlayList.setId(AudioFileFetcher.LOCAL_SONG_LIST_ID);

        defaultLocalPlayListDetail.setName(defaultLocalPlayList.getName());
        defaultLocalPlayListDetail.setDescription(defaultLocalPlayList.getDescription());
    }


    @Override
    public Single<List<PlaylistDTO>> fetchSongList(Integer userId) {
        /**
         * 这个本地扫描会调用很多次
         * 为了减缓系统数据库扫描，尝试做数据缓存，获取一次本地数据后便不再重复获取
         */
        List<AudioFileFetcher.AudioFile> localAudioFilesValue = _localAudioFiles.getValue();
        Single<List<AudioFileFetcher.AudioFile>> runnable;
        if (localAudioFilesValue != null && !localAudioFilesValue.isEmpty()) {
            //若有缓存，直接返回缓存
            runnable = Single.just(localAudioFilesValue);
        } else {
            runnable = Single.fromCallable(
                    new Callable<List<AudioFileFetcher.AudioFile>>() {
                        @Override
                        public List<AudioFileFetcher.AudioFile> call() throws Exception {
                            List<AudioFileFetcher.AudioFile> audioFiles = AudioFileFetcher.getAudioFiles(application.get());
                            _localAudioFiles.postValue(audioFiles);
                            return audioFiles;
                        }
                    }
            );
        }
        return runnable.flatMap(new Function<List<AudioFileFetcher.AudioFile>, SingleSource<List<PlaylistDTO>>>() {
            @Override
            public SingleSource<List<PlaylistDTO>> apply(List<AudioFileFetcher.AudioFile> audioFiles) throws Throwable {
                defaultLocalPlayList.setTrackCount(audioFiles.size());
                return Single.just(Collections.singletonList(defaultLocalPlayList));
            }
        });
    }

    @Override
    public Single<TypeWrapper<UserMusicListBean.PlayList>> fetchSongListDetail(PlaylistDTO songList) {
        List<AudioFileFetcher.AudioFile> localAudioFilesValue = localAudioFiles.getValue();

        if (localAudioFilesValue == null || localAudioFilesValue.isEmpty()) {
            return Single.just(TypeWrapper.local(defaultLocalPlayListDetail));
        }

        List<UserMusicListBean.PlayList.Tracks> trackList = new ArrayList<>();
        for (int i = 0; i < localAudioFilesValue.size(); i++) {
            AudioFileFetcher.AudioFile audioFile = localAudioFilesValue.get(i);
            UserMusicListBean.PlayList.Tracks track = new UserMusicListBean.PlayList.Tracks();
            track.setId(String.valueOf(i));
            track.setName(audioFile.getName());
            UserMusicListBean.PlayList.Tracks.Al al = new UserMusicListBean.PlayList.Tracks.Al();
            al.setPicUrl(audioFile.getAlbum());
            track.setAl(al);
            UserMusicListBean.PlayList.Tracks.Ar ar = new UserMusicListBean.PlayList.Tracks.Ar();
            ar.setName(audioFile.getArtist());
            track.setAr(Collections.singletonList(ar));

            trackList.add(track);
        }

        defaultLocalPlayList.setTrackCount(trackList.size());
        defaultLocalPlayListDetail.setTracks(trackList);
        return Single.just(TypeWrapper.local(defaultLocalPlayListDetail));

//        List<MediaSessionCompat.QueueItem> musicList = new ArrayList<>();
//        for (int i = 0; i < localAudioFilesValue.size(); i++) {
//            MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
//                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, localAudioFilesValue.get(i).getName())//歌名
//                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, localAudioFilesValue.get(i).getArtist())//歌手
//                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, localAudioFilesValue.get(i).getAlbum())//歌曲封面
//                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, localAudioFilesValue.get(i).getId() + "")//歌曲id
//                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, localAudioFilesValue.get(i).getPath())
//                    .build();
//
//            MediaDescriptionCompat desc = metadata.getDescription();
//
//            Bundle extras = new Bundle();
//            extras.putInt(MusicService.MSCQIMusicType, SongType.LOCAL.type);  // 关键在这里
//
//            MediaDescriptionCompat finalDesc = new MediaDescriptionCompat.Builder()
//                    .setMediaId(desc.getMediaId())
//                    .setTitle(desc.getTitle())
//                    .setSubtitle(desc.getSubtitle())
//                    .setIconUri(desc.getIconUri())
//                    .setDescription(desc.getDescription())
//                    .setExtras(extras)  // 手动设置
//                    .build();
//
//            musicList.add(new MediaSessionCompat.QueueItem(finalDesc, i));
//        }
//        return Single.just(musicList);
    }

    @Override
    public Single<Boolean> changeMusicLikeState(boolean like, int id) {
        return Single.just(Boolean.TRUE);
    }

    @Override
    public Single<Resource<MusicUrlDTO.DataDTO>> getMusicUrl(Long musicId) {
        List<AudioFileFetcher.AudioFile> localAudioFilesValue = localAudioFiles.getValue();
        if (localAudioFilesValue == null || localAudioFilesValue.isEmpty())
            return Single.just(new Resource.Empty<>());

        Optional<AudioFileFetcher.AudioFile> first = localAudioFilesValue
                .parallelStream()
                .filter(new Predicate<AudioFileFetcher.AudioFile>() {
                    @Override
                    public boolean test(AudioFileFetcher.AudioFile audioFile) {
                        return audioFile.getId() == musicId;
                    }
                })
                .findFirst();
        if (first.isPresent()) {
            MusicUrlDTO.DataDTO dataDTO = new MusicUrlDTO.DataDTO();
            dataDTO.setUrl(first.get().getPath());
            return Single.just(new Resource.Success<>(dataDTO));
        }
        return Single.just(new Resource.Error<>("播放音乐失败：未找到音乐数据"));
    }
}

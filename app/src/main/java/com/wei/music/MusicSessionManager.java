package com.wei.music;

import android.support.v4.media.session.MediaSessionCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wei.music.bean.PlaylistDTO;
import com.wei.music.repository.MusicListRepository;
import com.wei.music.service.musicaction.MusicActionContract;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;

public class MusicSessionManager {

    //用户的歌单
    private final MutableLiveData<List<PlaylistDTO>> _allPlaylistData = new MutableLiveData<>();
    public final LiveData<List<PlaylistDTO>> allPlaylistData = _allPlaylistData;

    public final MutableLiveData<MusicActionContract> intent = new MutableLiveData<>();
    private final MutableLiveData<List<MediaSessionCompat.QueueItem>> PLAYING_ITEMS = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<MediaSessionCompat.QueueItem> PLAYING_ITEM = new MutableLiveData<>();
//    public final MediatorLiveData<MusicState> musicStateMediatorLiveData = new MediatorLiveData<>();

    private final MusicListRepository musicListRepository;

    @Inject
    public MusicSessionManager(MusicListRepository musicListRepository) {
        this.musicListRepository = musicListRepository;

//        musicStateMediatorLiveData.addSource(PLAYING_ITEMS, new Observer<List<MediaSessionCompat.QueueItem>>() {
//            @Override
//            public void onChanged(List<MediaSessionCompat.QueueItem> queueItems) {
//                MediaSessionCompat.QueueItem currentItem = PLAYING_ITEM.getValue();
//                musicStateMediatorLiveData.setValue(new MusicState(queueItems, currentItem));
//            }
//        });
//        musicStateMediatorLiveData.addSource(PLAYING_ITEM, new Observer<MediaSessionCompat.QueueItem>() {
//            @Override
//            public void onChanged(MediaSessionCompat.QueueItem queueItem) {
//                List<MediaSessionCompat.QueueItem> queueItems = PLAYING_ITEMS.getValue();
//                musicStateMediatorLiveData.setValue(new MusicState(queueItems, queueItem));
//            }
//        });

    }

    public Observable<List<PlaylistDTO>> loadDatabaseSongList() {
        return musicListRepository.loadDatabaseSongList()
                .map(new Function<List<PlaylistDTO>, List<PlaylistDTO>>() {
                    @Override
                    public List<PlaylistDTO> apply(List<PlaylistDTO> playlistDTOS) throws Throwable {
                        _allPlaylistData.postValue(playlistDTOS);
                        return playlistDTOS;
                    }
                })
                .onErrorReturn(new Function<Throwable, List<PlaylistDTO>>() {
                    @Override
                    public List<PlaylistDTO> apply(Throwable throwable) throws Throwable {
                        _allPlaylistData.postValue(Collections.emptyList());
                        return Collections.emptyList();
                    }
                });
    }

    public Single<List<PlaylistDTO>> loadLocalSongList() {
        return musicListRepository.fetchLocalSongList();
    }


    //Observable<List<PlaylistDTO>>
//    public void loadUserSongList(Integer userId) {
//        Disposable subscribe = musicListRepository.loadSongList(userId)
//                .onErrorReturn(new Function<Throwable, List<PlaylistDTO>>() {
//                    @Override
//                    public List<PlaylistDTO> apply(Throwable throwable) throws Throwable {
//                        _allPlaylistData.postValue(Collections.emptyList());
//                        return Collections.emptyList();
//                    }
//                })
//                .subscribe(new Consumer<List<PlaylistDTO>>() {
//                    @Override
//                    public void accept(List<PlaylistDTO> playlistDTOS) throws Throwable {
//                        _allPlaylistData.postValue(playlistDTOS);
//                    }
//                });
//
//    }


    public void refreshSongListWithUser(int userId) {
        musicListRepository.fetchAllSongList(userId)
                .subscribe();
    }
}

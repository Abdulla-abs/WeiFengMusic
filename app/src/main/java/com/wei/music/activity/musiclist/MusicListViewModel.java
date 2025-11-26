package com.wei.music.activity.musiclist;

import android.support.v4.media.session.MediaSessionCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.UserMusicListBean;
import com.wei.music.mapper.PlayListMapper;
import com.wei.music.repository.MusicListRepository;
import com.wei.music.service.wrapper.TypeWrapper;
import com.wei.music.utils.Resource;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

@HiltViewModel
public class MusicListViewModel extends ViewModel {

    private final MusicListRepository repository;

    private final MutableLiveData<Resource<UserMusicListBean.PlayList>> _playListDetail
            = new MutableLiveData<>(new Resource.Empty<>());
    public final LiveData<Resource<UserMusicListBean.PlayList>> playListDetail = _playListDetail;

    private final MutableLiveData<List<MediaSessionCompat.QueueItem>> _playListDetailQueue
            = new MutableLiveData<>();
    public final LiveData<List<MediaSessionCompat.QueueItem>> playListDetailQueue = _playListDetailQueue;


    @Inject
    public MusicListViewModel(MusicListRepository repository) {
        this.repository = repository;
    }

    public void fetchPlayListDetail(PlaylistDTO playlistDTO) {
        _playListDetail.postValue(new Resource.Loading<>());
        Disposable subscribe = repository.fetchSongListDetail(playlistDTO)
                .subscribe(new Consumer<TypeWrapper<UserMusicListBean.PlayList>>() {
                    @Override
                    public void accept(TypeWrapper<UserMusicListBean.PlayList> playList) throws Throwable {
                        _playListDetailQueue.postValue(PlayListMapper.mapper(playList));
                        _playListDetail.postValue(new Resource.Success<>(playList.getData()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        _playListDetailQueue.postValue(Collections.emptyList());
                        _playListDetail.postValue(new Resource.Error<>(throwable.getMessage()));
                    }
                });
    }
}

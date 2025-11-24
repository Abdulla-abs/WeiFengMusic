package com.wei.music.activity.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wei.music.bean.SearchHistoryVO;
import com.wei.music.bean.SearchResultDTO;
import com.wei.music.network.NestedApi;
import com.wei.music.repository.SearchHistoryRepository;
import com.wei.music.utils.Resource;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class SearchResultViewModel extends ViewModel {

    private final SearchHistoryRepository repository;

    private final MutableLiveData<Resource<SearchResultDTO.ResultDTO>> _searchResultLiveData =
            new MutableLiveData<>();
    public final LiveData<Resource<SearchResultDTO.ResultDTO>> searchResultLiveData = _searchResultLiveData;

    @Inject
    public SearchResultViewModel(SearchHistoryRepository repository) {
        this.repository = repository;

    }

    public void onSearchSong(String content) {
        _searchResultLiveData.postValue(new Resource.Loading<>());
        Disposable subscribe = repository.searchSong(content)
                .subscribe(new Consumer<Resource<SearchResultDTO.ResultDTO>>() {
                    @Override
                    public void accept(Resource<SearchResultDTO.ResultDTO> resultDTOResource) throws Throwable {
                        _searchResultLiveData.postValue(resultDTOResource);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        _searchResultLiveData.postValue(new Resource.Error<>(throwable.getMessage()));
                    }
                });

    }
}

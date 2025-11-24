package com.wei.music.activity.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.LogUtils;
import com.wei.music.bean.SearchHistoryVO;
import com.wei.music.repository.SearchHistoryRepository;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class SearchViewModel extends ViewModel {

    private final SearchHistoryRepository historyRepository;

    private final MutableLiveData<List<SearchHistoryVO>> _searchHistory =
            new MutableLiveData<>();
    public final LiveData<List<SearchHistoryVO>> searchHistory = _searchHistory;
    private Disposable historyWatcher;

    public final MutableLiveData<SearchIntent> searchIntentMutableLiveData =
            new MutableLiveData<>(new SearchIntent.IDLE());


    @Inject
    public SearchViewModel(SearchHistoryRepository repository) {
        historyRepository = repository;
        observerSearchHistory();
    }

    private void observerSearchHistory() {
        historyWatcher = historyRepository.observableSearchHistory()
                .switchIfEmpty(Observable.just(Collections.emptyList())) //room数据库不会在数据为空时发送空的数据集合
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<SearchHistoryVO>>() {
                    @Override
                    public void accept(List<SearchHistoryVO> searchHistoryVOS) throws Throwable {
                        _searchHistory.postValue(searchHistoryVOS);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {

                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        historyWatcher.dispose();
    }


    public void storeSearchKeywords(SearchIntent.Search search) {
        SearchHistoryVO searchHistoryVO = new SearchHistoryVO();
        searchHistoryVO.setSearchTime(Calendar.getInstance().getTime());
        searchHistoryVO.setContent(search.searchKeyWorlds);
        Observable.fromAction(new Action() {
            @Override
            public void run() throws Throwable {
                historyRepository.storeSearchHistory(searchHistoryVO);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}

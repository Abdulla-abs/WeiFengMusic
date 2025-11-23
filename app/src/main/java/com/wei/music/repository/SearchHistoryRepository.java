package com.wei.music.repository;

import com.wei.music.bean.SearchHistoryVO;
import com.wei.music.database.dao.SearchHistoryDao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class SearchHistoryRepository {

    private final SearchHistoryDao searchHistoryDao;

    @Inject
    public SearchHistoryRepository(SearchHistoryDao searchHistoryDao) {
        this.searchHistoryDao = searchHistoryDao;
    }


    public Observable<List<SearchHistoryVO>> observableSearchHistory() {
        return searchHistoryDao.observerSearchHistory();
    }

    public Single<List<SearchHistoryVO>> querySearchHistory() {
        return searchHistoryDao.querySearchHistory();
    }

    public void storeSearchHistory(SearchHistoryVO searchHistoryVO) {
        searchHistoryDao.storeHistory(searchHistoryVO);
    }

    public void deleteHistory(SearchHistoryVO searchHistoryVO) {
        searchHistoryDao.deleteHistory(searchHistoryVO);
    }

    public void deleteHistory(int id) {
        searchHistoryDao.deleteHistory(id);
    }

    public void clearHistory() {
        searchHistoryDao.clear();
    }

}

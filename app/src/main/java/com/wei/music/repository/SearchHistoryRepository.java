package com.wei.music.repository;

import com.wei.music.bean.SearchHistoryVO;
import com.wei.music.bean.SearchResultDTO;
import com.wei.music.database.dao.SearchHistoryDao;
import com.wei.music.network.NestedApi;
import com.wei.music.utils.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class SearchHistoryRepository {

    private final SearchHistoryDao searchHistoryDao;

    private final NestedApi nestedApi;

    @Inject
    public SearchHistoryRepository(NestedApi nestedApi, SearchHistoryDao searchHistoryDao) {
        this.nestedApi = nestedApi;
        this.searchHistoryDao = searchHistoryDao;
    }

    public Observable<List<SearchHistoryVO>> observableSearchHistory() {
        return searchHistoryDao.observerSearchHistory(10);
    }

    public Single<List<SearchHistoryVO>> querySearchHistory() {
        return searchHistoryDao.querySearchHistory(10);
    }

    public void storeSearchHistory(SearchHistoryVO searchHistoryVO) {
        searchHistoryDao.storeHistory(searchHistoryVO);
    }

    public void deleteHistory(SearchHistoryVO searchHistoryVO) {
        searchHistoryDao.deleteHistory(searchHistoryVO);
    }

    public void deleteHistory(String content) {
        searchHistoryDao.deleteHistory(content);
    }

    public void clearHistory() {
        searchHistoryDao.clear();
    }

    public Single<Resource<SearchResultDTO.ResultDTO>> searchSong(String content) {
        return nestedApi.searchMusic(content, 30, 0, 1).subscribeOn(Schedulers.io())
                .map(new Function<SearchResultDTO, Resource<SearchResultDTO.ResultDTO>>() {
                    @Override
                    public Resource<SearchResultDTO.ResultDTO> apply(SearchResultDTO searchResultDTO) throws Throwable {
                        if (searchResultDTO.getCode() >= 200 && searchResultDTO.getCode() < 300) {
                            return new Resource.Success<>(searchResultDTO.getResult());
                        }
                        return new Resource.Error<>("Occur Error Code :" + searchResultDTO.getCode());
                    }
                });
    }
}

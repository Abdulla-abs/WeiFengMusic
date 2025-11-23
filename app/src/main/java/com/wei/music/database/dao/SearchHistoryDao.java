package com.wei.music.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.wei.music.bean.SearchHistoryVO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SearchHistoryDao {

    @Query("SELECT * FROM SEARCHHISTORYVO")
    public Observable<List<SearchHistoryVO>> observerSearchHistory();

    @Query("SELECT * FROM SEARCHHISTORYVO")
    public Single<List<SearchHistoryVO>> querySearchHistory();

    @Insert
    public void storeHistory(SearchHistoryVO historyVO);

    @Delete
    public void deleteHistory(SearchHistoryVO historyVO);

    @Query("DELETE FROM SEARCHHISTORYVO WHERE id == :id")
    public void deleteHistory(int id);

    @Query("DELETE FROM SEARCHHISTORYVO")
    public void clear();

}

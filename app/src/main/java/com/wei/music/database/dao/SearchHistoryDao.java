package com.wei.music.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.wei.music.bean.SearchHistoryVO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SearchHistoryDao {

    @Query("SELECT * FROM SEARCHHISTORYVO ORDER BY searchTime DESC LIMIT :limit")
    public Observable<List<SearchHistoryVO>> observerSearchHistory(int limit);

    @Query("SELECT * FROM SEARCHHISTORYVO ORDER BY searchTime DESC LIMIT :limit")
    public Single<List<SearchHistoryVO>> querySearchHistory(int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void storeHistory(SearchHistoryVO historyVO);

    @Delete
    public void deleteHistory(SearchHistoryVO historyVO);

    @Query("DELETE FROM SEARCHHISTORYVO WHERE content == :content")
    public void deleteHistory(String content);

    @Query("DELETE FROM SEARCHHISTORYVO")
    public void clear();

}

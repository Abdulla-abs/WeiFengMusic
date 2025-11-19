package com.wei.music.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.wei.music.bean.PlaylistDTO;

import java.util.List;

@Dao
public interface UserSubCountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PlaylistDTO> playlists);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlaylistDTO playlist);

    @Query("SELECT * FROM PlaylistDTO ORDER BY createTime DESC")
    LiveData<List<PlaylistDTO>> getAllPlaylists();

    @Query("DELETE FROM PlaylistDTO")
    void deleteAll();
}

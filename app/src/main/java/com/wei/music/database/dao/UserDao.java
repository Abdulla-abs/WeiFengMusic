package com.wei.music.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.wei.music.bean.UserLoginBean;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserLoginBean userLoginBean);

    @Query("SELECT * FROM UserLoginBean ORDER BY roomId DESC")
    Single<UserLoginBean> getCurrentUser();

    @Query("DELETE FROM UserLoginBean")
    void deleteAll();


}

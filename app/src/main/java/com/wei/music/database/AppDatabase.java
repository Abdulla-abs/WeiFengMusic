package com.wei.music.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.SearchHistoryVO;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.database.dao.SearchHistoryDao;
import com.wei.music.database.dao.UserDao;
import com.wei.music.database.dao.UserSubCountDao;
import com.wei.music.database.typeconverter.PlaylistConverters;
import com.wei.music.database.typeconverter.SearchHistoryConverters;
import com.wei.music.database.typeconverter.UserDtoConverters;

@Database(
        entities = {PlaylistDTO.class, UserLoginBean.class, SearchHistoryVO.class},
        version = 5,
        exportSchema = false
)
@TypeConverters({
        PlaylistConverters.class, UserDtoConverters.class, SearchHistoryConverters.class
})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract UserSubCountDao userSubCountDao();

    public abstract SearchHistoryDao searchHistoryDao();


    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "weifengmusic.db"   // 数据库文件名
                )
                // 开发阶段可以用这个，上线前一定要写 Migration
                .fallbackToDestructiveMigration()
                .build();
    }
}
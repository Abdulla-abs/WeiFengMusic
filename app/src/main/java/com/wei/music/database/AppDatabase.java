package com.wei.music.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.wei.music.bean.PlaylistDTO;
import com.wei.music.database.dao.UserSubCountDao;
import com.wei.music.database.typeconverter.PlaylistConverters;

@Database(entities = {PlaylistDTO.class}, version = 1, exportSchema = false)
@TypeConverters({PlaylistConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserSubCountDao userDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "weifengmusic.db"   // 数据库文件名
                            )
                            // 开发阶段可以用这个，上线前一定要写 Migration
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
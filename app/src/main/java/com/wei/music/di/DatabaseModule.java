package com.wei.music.di;

import android.content.Context;

import com.wei.music.App;
import com.wei.music.database.AppDatabase;
import com.wei.music.database.dao.UserDao;
import com.wei.music.database.dao.UserSubCountDao;
import com.wei.music.di.annotation.ApplicationContext;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class DatabaseModule {

    @Provides
    public static AppDatabase providerAppDatabase(@ApplicationContext Context application) {
        return AppDatabase.getInstance(application);
    }

    @Provides
    public static UserSubCountDao providerUserSubCountDao(AppDatabase appDatabase) {
        return appDatabase.userSubCountDao();
    }

    @Provides
    public static UserDao providerUserDao(AppDatabase appDatabase) {
        return appDatabase.userDao();
    }

}

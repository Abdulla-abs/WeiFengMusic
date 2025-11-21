package com.wei.music.di;

import android.app.Application;

import com.wei.music.App;
import com.wei.music.database.AppDatabase;
import com.wei.music.di.viewmodel.ViewModelBindingModule;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import jakarta.inject.Singleton;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        NetWorkModule.class,
        DatabaseModule.class,
        RepositoryModule.class,
        ActivityBindingModule.class,
        ServiceBindingModule.class
})
public interface AppComponent extends AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

}

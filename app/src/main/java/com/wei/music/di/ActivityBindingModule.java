package com.wei.music.di;

import com.wei.music.activity.main.MainActivity;
import com.wei.music.di.annotation.ActivityScope;
import com.wei.music.di.viewmodel.ViewModelBindingModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import jakarta.inject.Singleton;

@Module
public abstract class ActivityBindingModule {


    @ContributesAndroidInjector(modules = {MainFragmentBindingModule.class, ViewModelBindingModule.class})
    public abstract MainActivity contributesMainActivity();

}

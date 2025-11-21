package com.wei.music.di;

import com.wei.music.fragment.home.HomeFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import jakarta.inject.Singleton;

@Module
public abstract class MainFragmentBindingModule {

//    @Singleton
    @ContributesAndroidInjector
    public abstract HomeFragment contributeHomeFragment();


}

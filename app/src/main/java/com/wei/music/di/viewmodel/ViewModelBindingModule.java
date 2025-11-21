package com.wei.music.di.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wei.music.activity.main.MainActivityViewModel;
import com.wei.music.activity.musiclist.MusicListViewModel;
import com.wei.music.di.MainFragmentBindingModule;
import com.wei.music.di.annotation.ViewModelKey;
import com.wei.music.fragment.home.HomeViewModel;

import dagger.Module;
import dagger.Binds;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelBindingModule {

    @Binds
    public abstract ViewModelProvider.Factory bindsViewModelProviderFactory(ViewModelModuleFactory factory);


    @Binds
    @IntoMap
    @ViewModelKey(value = MainActivityViewModel.class)
    public abstract ViewModel bindMainActivityViewModel(MainActivityViewModel mainActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(value = HomeViewModel.class)
    public abstract ViewModel bindHomeFragmentViewModel(HomeViewModel homeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(value = MusicListViewModel.class)
    public abstract ViewModel bindMusicListViewModel(MusicListViewModel listViewModel);

}

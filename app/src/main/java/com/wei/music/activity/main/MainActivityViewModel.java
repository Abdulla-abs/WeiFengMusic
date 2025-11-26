package com.wei.music.activity.main;

import androidx.lifecycle.ViewModel;

import com.wei.music.AppSessionManager;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityViewModel extends ViewModel {

    private final AppSessionManager appSessionManager;

    @Inject
    public MainActivityViewModel(AppSessionManager appSessionManager) {
        this.appSessionManager = appSessionManager;
    }

    public void init() {
        appSessionManager.init();
    }
}

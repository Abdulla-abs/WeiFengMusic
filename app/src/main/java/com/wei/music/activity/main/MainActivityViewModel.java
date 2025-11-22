package com.wei.music.activity.main;

import com.wei.music.AppSessionManager;
import com.wei.music.utils.ViewModelScopeProviderUtil;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityViewModel extends ViewModelScopeProviderUtil.ScopedViewModel {

    private final AppSessionManager appSessionManager;

    @Inject
    public MainActivityViewModel(AppSessionManager appSessionManager) {
        this.appSessionManager = appSessionManager;
    }

    public void init() {
        appSessionManager.init();
    }
}

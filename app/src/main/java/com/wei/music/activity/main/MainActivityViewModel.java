package com.wei.music.activity.main;

import com.wei.music.AppSessionManager;
import com.wei.music.utils.ViewModelScopeProviderUtil;

import jakarta.inject.Inject;

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

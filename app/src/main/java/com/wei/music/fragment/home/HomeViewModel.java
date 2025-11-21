package com.wei.music.fragment.home;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wei.music.AppSessionManager;
import com.wei.music.MusicSessionManager;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.repository.UserRepository;
import com.wei.music.utils.Resource;
import com.wei.music.utils.ViewModelScopeProviderUtil;

import java.util.List;

import autodispose2.AutoDispose;
import io.reactivex.rxjava3.functions.Consumer;
import jakarta.inject.Inject;

public class HomeViewModel extends ViewModelScopeProviderUtil.ScopedViewModel {
    private static final String TAG = "HomeViewModel";
    private final MutableLiveData<Resource<Boolean>> _captchaLiveData = new MutableLiveData<>();
    public LiveData<Resource<Boolean>> captchaLiveData = _captchaLiveData;
    public final LiveData<List<PlaylistDTO>> allPlaylistData;
    public final LiveData<Resource<UserLoginBean>> userLoginLiveData;

    private UserRepository userRepository;
    private AppSessionManager appSessionManager;
    private MusicSessionManager musicSessionManager;

    @Inject
    public HomeViewModel(UserRepository userRepository, AppSessionManager appSessionManager,
                         MusicSessionManager musicSessionManager) {
        this.userRepository = userRepository;
        this.appSessionManager = appSessionManager;
        this.musicSessionManager = musicSessionManager;

        allPlaylistData = musicSessionManager.allPlaylistData;
        userLoginLiveData = appSessionManager.userLoginLiveData;
    }

    public void requestCaptcha(String phone) {
        _captchaLiveData.setValue(new Resource.Loading<>());

        if (TextUtils.isEmpty(phone)) {
            _captchaLiveData.setValue(new Resource.Error<>("请填写正确的电话号码"));
            return;
        }

        userRepository.requestCaptcha(phone)
                .to(AutoDispose.autoDisposable(getScopeProvider()))
                .subscribe(new Consumer<Resource<Void>>() {
                    @Override
                    public void accept(Resource<Void> voidResource) throws Throwable {
                        _captchaLiveData.postValue(new Resource.Success<>(voidResource.isSuccess()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        _captchaLiveData.postValue(new Resource.Success<>(Boolean.FALSE));
                    }
                });
    }

    public void observableSongListStore() {
        musicSessionManager.loadDatabaseSongList()
                .to(AutoDispose.autoDisposable(getScopeProvider()))
                .subscribe();
    }

//    public void loadUserSongList() {
//        Resource<UserLoginBean> value = userLoginLiveData.getValue();
//        if (value != null && value.isSuccess()) {
//            UserLoginBean valueData = value.getData();
//            musicSessionManager.loadUserSongList(valueData.getProfile().getUserId());
//        }
//    }

    public void refreshSongList() {
        Resource<UserLoginBean> value = userLoginLiveData.getValue();
        if (value != null && value.isSuccess()) {
            //已经登录的，刷新远程列表
            UserLoginBean valueData = value.getData();
            musicSessionManager.refreshSongListWithUser(valueData.getProfile().getUserId());
        }else {
            //重新查一遍本地音乐
            musicSessionManager.loadLocalSongList().subscribe();
        }

    }

    public void login(String phone, String captcha) {
        appSessionManager.login(phone, captcha);
    }
}

package com.wei.music.fragment.home;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wei.music.AppSessionManager;
import com.wei.music.MusicSessionManager;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.repository.UserRepository;
import com.wei.music.utils.Resource;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;

@HiltViewModel
public class HomeViewModel extends ViewModel {
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

        Disposable subscribe = userRepository.requestCaptcha(phone)
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
        } else {
            //重新查一遍本地音乐
            musicSessionManager.loadLocalSongList().subscribe();
        }

    }

    public void login(String phone, String captcha) {
        appSessionManager.login(phone, captcha);
    }
}

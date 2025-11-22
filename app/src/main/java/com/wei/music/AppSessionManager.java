package com.wei.music;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wei.music.bean.UserLoginBean;
import com.wei.music.repository.UserRepository;
import com.wei.music.utils.Resource;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

@Singleton
public class AppSessionManager {

    private final MutableLiveData<Resource<UserLoginBean>> _userLoginLiveData = new MutableLiveData<>(new Resource.Empty<>());
    public final LiveData<Resource<UserLoginBean>> userLoginLiveData = _userLoginLiveData;

    @Inject
    UserRepository userRepository;

    @Inject
    public AppSessionManager() {
    }

    public void init() {
        Disposable subscribe = userRepository.getCurrentUser()
                .subscribe(new Consumer<Resource<UserLoginBean>>() {
                    @Override
                    public void accept(Resource<UserLoginBean> userLoginBeanResource) throws Throwable {
                        _userLoginLiveData.postValue(userLoginBeanResource);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        _userLoginLiveData.postValue(new Resource.Error<>(throwable.getMessage()));
                    }
                });
    }

    public void login(String phone, String captcha) {
        _userLoginLiveData.postValue(new Resource.Loading<>());
        Disposable subscribe = userRepository.login(phone, captcha)
                .subscribe(new Consumer<Resource<UserLoginBean>>() {
                    @Override
                    public void accept(Resource<UserLoginBean> userLoginBeanResource) throws Throwable {
                        _userLoginLiveData.postValue(userLoginBeanResource);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        _userLoginLiveData.postValue(new Resource.Error<>(throwable.getMessage()));
                    }
                });
    }
}

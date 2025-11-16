package com.wei.music;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wei.music.bean.UserLoginBean;
import com.wei.music.network.ApiService;
import com.wei.music.utils.MMKVUtils;
import com.wei.music.utils.Resource;
import com.wei.music.utils.RxSchedulers;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AppSessionManager {

    private final MutableLiveData<Resource<UserLoginBean>> _userLoginLiveData = new MutableLiveData<>();
    public final LiveData<Resource<UserLoginBean>> userLoginLiveData = _userLoginLiveData;

    private AppSessionManager() {

    }

    public void init() {
        _userLoginLiveData.setValue(new Resource.Empty<>());
        Disposable subscribe = Single.fromCallable(new Callable<Resource<UserLoginBean>>() {
                    @Override
                    public Resource<UserLoginBean> call() throws Exception {
                        UserLoginBean userLoginBean = MMKVUtils.getUserLoginBean();
                        if (userLoginBean == null || TextUtils.isEmpty(userLoginBean.getCookie()) || TextUtils.isEmpty(userLoginBean.getToken())) {
                            return new Resource.Empty<>();
                        } else {
                            return new Resource.Success<>(userLoginBean);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<Resource<UserLoginBean>, SingleSource<String>>() {
                    @Override
                    public SingleSource<String> apply(Resource<UserLoginBean> userLoginBeanResource) throws Throwable {
                        _userLoginLiveData.postValue(userLoginBeanResource);
                        if (userLoginBeanResource.isSuccess()){
                            UserLoginBean data = userLoginBeanResource.getData();
                            return getUserSongs(data.getProfile().getUserId());
                        }
                        return Single.never();
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Throwable {
                        //get user song list result
                        String s1 = s;
                    }
                });

    }

    /**
     * response 404
     * @param uid userId
     */
    private Single<String> getUserSongs(int uid) {
        return ApiService.ServiceHolder.service
                .getNestedApi()
                .getPlayList(uid)
                .compose(RxSchedulers.applySchedulers())
                .onErrorReturn(new Function<Throwable, String>() {
                    @Override
                    public String apply(Throwable throwable) throws Throwable {
                        return "no impl api";
                    }
                }).firstOrError();
    }
    public void login(String phone, String captcha) {
        _userLoginLiveData.postValue(new Resource.Loading<>());

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(captcha)) {
            _userLoginLiveData.postValue(new Resource.Error<>("填写手机号以及验证码后再尝试登录"));
            return;
        }

        Disposable subscribe = ApiService.ServiceHolder.service
                .getNestedApi()
                .captchaLogin(phone, captcha)
                .compose(RxSchedulers.applySchedulers())
                .onErrorReturn(new Function<Throwable, UserLoginBean>() {
                    @Override
                    public UserLoginBean apply(Throwable throwable) throws Throwable {
                        UserLoginBean userLoginBean = new UserLoginBean();
                        userLoginBean.setCode(500);
                        return userLoginBean;
                    }
                })
                .subscribe(new Consumer<UserLoginBean>() {
                    @Override
                    public void accept(UserLoginBean userLoginBean) throws Throwable {
                        if (userLoginBean.getCode() >= 200 && userLoginBean.getCode() < 300) {
                            MMKVUtils.putUser(userLoginBean);
                            _userLoginLiveData.postValue(new Resource.Success<>(userLoginBean));
                        } else {
                            _userLoginLiveData.postValue(new Resource.Error<>("登录失败，错误码：" + userLoginBean.getCode()));
                        }
                    }
                });
    }

    public static class Holder {
        public final static AppSessionManager instance = new AppSessionManager();

        private Holder() {

        }
    }

}

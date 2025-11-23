package com.wei.music.repository;

import com.wei.music.bean.BaseResp;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.database.dao.UserDao;
import com.wei.music.network.LoginApi;
import com.wei.music.network.NestedApi;
import com.wei.music.utils.Resource;
import com.wei.music.utils.RxSchedulers;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Response;

@Singleton
public class UserRepository {

    private final LoginApi loginApi;
    private final NestedApi nestedApi;
    private final UserDao userDao;

    @Inject
    public UserRepository(LoginApi loginApi, NestedApi nestedApi, UserDao userDao) {
        this.loginApi = loginApi;
        this.nestedApi = nestedApi;
        this.userDao = userDao;
    }

    public Observable<Resource<Void>> requestCaptcha(String phone) {
        return nestedApi.requestCaptcha(phone, "86")
                .compose(RxSchedulers.applySchedulers())
                .map(new Function<BaseResp<Boolean>, Resource<Void>>() {
                    @Override
                    public Resource<Void> apply(BaseResp<Boolean> booleanBaseResp) throws Throwable {
                        if (booleanBaseResp.success() && booleanBaseResp.getData()) {
                            return new Resource.Success<>(null);
                        }
                        return new Resource.Error<>(booleanBaseResp.getMessage());
                    }
                });
    }

    public Single<Resource<UserLoginBean>> getCurrentUser() {
        return userDao.getCurrentUser()
                .map(new Function<UserLoginBean, Resource<UserLoginBean>>() {
                    @Override
                    public Resource<UserLoginBean> apply(UserLoginBean userLoginBean) throws Throwable {
                        return new Resource.Success<>(userLoginBean);
                    }
                })
                .onErrorReturn(new Function<Throwable, Resource<UserLoginBean>>() {
                    @Override
                    public Resource<UserLoginBean> apply(Throwable throwable) throws Throwable {
                        return new Resource.Error<>(throwable.getMessage());
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public @NonNull Observable<Resource<UserLoginBean>> login(String phone, String captcha) {
        return loginApi.captchaLogin(phone, captcha)
                .map(new Function<Response<UserLoginBean>, Resource<UserLoginBean>>() {
                    @Override
                    public Resource<UserLoginBean> apply(Response<UserLoginBean> userLoginBeanResponse) throws Throwable {
                        if (userLoginBeanResponse.isSuccessful()) {
                            UserLoginBean body = userLoginBeanResponse.body();

                            userDao.insertUser(body);

                            return new Resource.Success<>(body);
                        }
                        userDao.deleteAll();

                        return new Resource.Error<>("验证码发送失败。错误码：" + userLoginBeanResponse.code());
                    }
                })
                .onErrorReturn(new Function<Throwable, Resource<UserLoginBean>>() {
                    @Override
                    public Resource<UserLoginBean> apply(Throwable throwable) throws Throwable {
                        return new Resource.Error<>(throwable.getMessage());
                    }
                })
                .subscribeOn(Schedulers.io());
    }
}

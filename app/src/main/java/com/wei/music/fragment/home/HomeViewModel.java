package com.wei.music.fragment.home;

import android.text.TextUtils;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wei.music.AppSessionManager;
import com.wei.music.bean.BaseResp;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.network.NestedService;
import com.wei.music.utils.MMKVUtils;
import com.wei.music.utils.Resource;
import com.wei.music.utils.RxSchedulers;
import com.wei.music.utils.ViewModelScopeProviderUtil;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;

public class HomeViewModel extends ViewModelScopeProviderUtil.ScopedViewModel {

    private final MutableLiveData<Resource<Boolean>> _captchaLiveData = new MutableLiveData<>();
    public LiveData<Resource<Boolean>> captchaLiveData = _captchaLiveData;

    public void requestCaptcha(String phone) {
        _captchaLiveData.setValue(new Resource.Loading<>());

        if (TextUtils.isEmpty(phone)) {
            _captchaLiveData.setValue(new Resource.Error<>("请填写正确的电话号码"));
            return;
        }

        NestedService.ServiceHolder.service
                .getApi()
                .requestCaptcha(phone, "86")
                .compose(RxSchedulers.applySchedulers())
                .onErrorReturn(new Function<Throwable, BaseResp<Boolean>>() {
                    @Override
                    public BaseResp<Boolean> apply(Throwable throwable) throws Throwable {
                        BaseResp<Boolean> booleanBaseResp = new BaseResp<>();
                        booleanBaseResp.setCode(400);
                        booleanBaseResp.setData(false);
                        booleanBaseResp.setMessage("Local request failure,please try again latter...");
                        return booleanBaseResp;
                    }
                })
                .to(AutoDispose.autoDisposable(getScopeProvider()))
                .subscribe(new Consumer<BaseResp<Boolean>>() {
                    @Override
                    public void accept(BaseResp<Boolean> resp) throws Throwable {
                        if (resp.success() && resp.getData()) {
                            _captchaLiveData.postValue(new Resource.Success<>(true));

                        } else {
                            _captchaLiveData.postValue(new Resource.Error<>(resp.getMessage()));
                        }
                    }
                });
    }

}

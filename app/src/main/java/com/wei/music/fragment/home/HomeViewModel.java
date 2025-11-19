package com.wei.music.fragment.home;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.wei.music.AppSessionManager;
import com.wei.music.bean.BaseResp;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.SongListBean;
import com.wei.music.network.ApiService;
import com.wei.music.utils.AudioFileFetcher;
import com.wei.music.utils.Resource;
import com.wei.music.utils.RxSchedulers;
import com.wei.music.utils.ViewModelScopeProviderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import autodispose2.AutoDispose;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends ViewModelScopeProviderUtil.ScopedViewModel {
    private static final String TAG = "HomeViewModel";
    private final MutableLiveData<Resource<Boolean>> _captchaLiveData = new MutableLiveData<>();
    public LiveData<Resource<Boolean>> captchaLiveData = _captchaLiveData;

    public void requestCaptcha(String phone) {
        _captchaLiveData.setValue(new Resource.Loading<>());

        if (TextUtils.isEmpty(phone)) {
            _captchaLiveData.setValue(new Resource.Error<>("请填写正确的电话号码"));
            return;
        }

        ApiService.ServiceHolder.service
                .getNestedApi()
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

    public void prepareSongListData(Context context) {
        AppSessionManager.Holder.instance.loadPlaylist(context);

//        Single.fromCallable(() -> AudioFileFetcher.getAudioFiles(context))
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .to(AutoDispose.autoDisposable(getScopeProvider()))
//                .subscribe(audioFiles -> {
//                            if (audioFiles.isEmpty()) return;
//                            Optional.ofNullable(_songList.getValue())
//                                    .ifPresent(new java.util.function.Consumer<List<SongListBean>>() {
//                                        @Override
//                                        public void accept(List<SongListBean> songListBeans) {
//                                            songListBeans.add(0, AudioFileFetcher.cachedLocalSongs);
//                                            _songList.postValue(new ArrayList<>(songListBeans));
//                                        }
//                                    });
//                        },
//                        throwable -> Log.e(TAG, "扫描本地音乐出错", throwable)
//                );
    }
}

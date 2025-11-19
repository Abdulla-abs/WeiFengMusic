package com.wei.music;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.bean.UserSubCount;
import com.wei.music.network.ApiService;
import com.wei.music.utils.AudioFileFetcher;
import com.wei.music.utils.MMKVUtils;
import com.wei.music.utils.Resource;
import com.wei.music.utils.RxSchedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Response;

public class AppSessionManager {

    private final MutableLiveData<Resource<UserLoginBean>> _userLoginLiveData = new MutableLiveData<>(new Resource.Empty<>());
    public final LiveData<Resource<UserLoginBean>> userLoginLiveData = _userLoginLiveData;

    private final MutableLiveData<List<PlaylistDTO>> _userPlayListData = new MutableLiveData<>();
    private final MutableLiveData<List<PlaylistDTO>> _localPlayListData = new MutableLiveData<>();
    private final MediatorLiveData<List<PlaylistDTO>> _allPlaylistData = new MediatorLiveData<>();
    public final LiveData<List<PlaylistDTO>> allPlaylistData = _allPlaylistData;
    public List<AudioFileFetcher.AudioFile> localAudioFiles;

    private AppSessionManager() {
        _allPlaylistData.addSource(_userPlayListData, new Observer<List<PlaylistDTO>>() {
            @Override
            public void onChanged(List<PlaylistDTO> playlistDTOS) {
                List<PlaylistDTO> mergedPlaylistData = mergedPlaylistData(_localPlayListData.getValue(), playlistDTOS);
                _allPlaylistData.setValue(mergedPlaylistData);
            }
        });
        _allPlaylistData.addSource(_localPlayListData, new Observer<List<PlaylistDTO>>() {
            @Override
            public void onChanged(List<PlaylistDTO> playlistDTOS) {
                List<PlaylistDTO> mergedPlaylistData = mergedPlaylistData(playlistDTOS, _userPlayListData.getValue());
                _allPlaylistData.setValue(mergedPlaylistData);
            }
        });
    }

    public void loadPlaylist(Context context) {
        ArrayList<ObservableSource<List<PlaylistDTO>>> op = new ArrayList<>();

        Resource<UserLoginBean> user = userLoginLiveData.getValue();
        if (user != null && user.isSuccess()) {
            op.add(
                    getUserSongs(user.getData().getProfile().getUserId())
                            .map(new Function<Response<UserSubCount>, List<PlaylistDTO>>() {
                                @Override
                                public List<PlaylistDTO> apply(Response<UserSubCount> userSubCountResponse) throws Throwable {
                                    UserSubCount userSubCount = userSubCountResponse.body();
                                    if (userSubCountResponse.isSuccessful() && userSubCount != null) {
                                        return userSubCount.getPlaylist();
                                    }
                                    return Collections.emptyList();
                                }
                            })
            );
        }
        op.add(loadLocalPlayList(context));
        Disposable subscribe = Observable.mergeDelayError(
                op, 2
        ).subscribe(new Consumer<List<PlaylistDTO>>() {
            @Override
            public void accept(List<PlaylistDTO> playlistDTOS) throws Throwable {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {

            }
        });
    }

    private List<PlaylistDTO> mergedPlaylistData(List<PlaylistDTO> local, List<PlaylistDTO> remote) {
        if (local == null && remote == null) return new ArrayList<>();
        if (local == null) return remote != null ? remote : new ArrayList<>();
        if (remote == null) return local;
        ArrayList<PlaylistDTO> playlistDTOS = new ArrayList<>();
        playlistDTOS.addAll(local);
        playlistDTOS.addAll(remote);
        return playlistDTOS;
    }

    private Observable<List<PlaylistDTO>> loadLocalPlayList(Context context) {
        return Observable.fromCallable(new Callable<List<PlaylistDTO>>() {
            @Override
            public List<PlaylistDTO> call() throws Exception {
                PlaylistDTO localMusic = AudioFileFetcher.getLocalMusic();
                localAudioFiles = AudioFileFetcher.getAudioFiles(context);
                localMusic.setTrackCount(localAudioFiles.size());

                return Collections.singletonList(localMusic);
            }
        }).map(new Function<List<PlaylistDTO>, List<PlaylistDTO>>() {
            @Override
            public List<PlaylistDTO> apply(List<PlaylistDTO> playlistDTOS) throws Throwable {
                _localPlayListData.postValue(playlistDTOS);
                return playlistDTOS;
            }
        });
    }

    public void init() {
        _userLoginLiveData.setValue(new Resource.Empty<>());
        Disposable subscribe = Observable.fromCallable(new Callable<Resource<UserLoginBean>>() {
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
                .subscribe(new Consumer<Resource<UserLoginBean>>() {
                    @Override
                    public void accept(Resource<UserLoginBean> userLoginBeanResource) throws Throwable {
                        _userLoginLiveData.postValue(userLoginBeanResource);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {

                    }
                });

    }

    /**
     * response 404
     * @param uid userId
     */
    private Observable<Response<UserSubCount>> getUserSongs(int uid) {
        return ApiService.ServiceHolder.service
                .getNestedApi()
                .getPlayList(uid)
                .compose(RxSchedulers.applySchedulers())
                .map(new Function<Response<UserSubCount>, Response<UserSubCount>>() {
                    @Override
                    public Response<UserSubCount> apply(Response<UserSubCount> userSubCountResponse) throws Throwable {
                        UserSubCount userSubCount = userSubCountResponse.body();

                        if (userSubCountResponse.isSuccessful() && userSubCount != null) {
                            _userPlayListData.postValue(userSubCount.getPlaylist());
                            App.getDatabase().userDao()
                                    .deleteAll();
                            App.getDatabase().userDao()
                                    .insertAll(userSubCount.getPlaylist());
                        } else {
                            _userPlayListData.postValue(Collections.emptyList());
                        }
                        return userSubCountResponse;
                    }
                });
    }

    public void login(String phone, String captcha) {
        _userLoginLiveData.postValue(new Resource.Loading<>());

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(captcha)) {
            _userLoginLiveData.postValue(new Resource.Error<>("填写手机号以及验证码后再尝试登录"));
            return;
        }

        Disposable subscribe = ApiService.ServiceHolder.service
                .getLoginApi()
                .captchaLogin(phone, captcha)
                .flatMap(new Function<Response<UserLoginBean>, Observable<Response<UserSubCount>>>() {
                    @Override
                    public Observable<Response<UserSubCount>> apply(Response<UserLoginBean> userLoginBeanResponse) throws Throwable {
                        if (userLoginBeanResponse.isSuccessful()) {
                            MMKVUtils.putUser(userLoginBeanResponse.body());
                            _userLoginLiveData.postValue(new Resource.Success<>(userLoginBeanResponse.body()));
                            return getUserSongs(userLoginBeanResponse.body().getAccount().getId());
                        } else {
                            //userLoginBeanResponse.errorBody()
                            _userLoginLiveData.postValue(new Resource.Error<>("登录失败，错误码："));
                            return Observable.error(new Exception(userLoginBeanResponse.errorBody().string()));
                        }
                    }
                })
                .compose(RxSchedulers.applySchedulers())
                .subscribe(new Consumer<Response<UserSubCount>>() {
                    @Override
                    public void accept(Response<UserSubCount> userSubCountResponse) throws Throwable {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        throwable.printStackTrace();
                    }
                });
    }

    public static class Holder {
        public final static AppSessionManager instance = new AppSessionManager();

        private Holder() {

        }
    }

}

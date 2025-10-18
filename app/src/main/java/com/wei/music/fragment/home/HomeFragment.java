package com.wei.music.fragment.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.wei.music.AppSessionManager;
import com.wei.music.R;
import com.wei.music.activity.MusicListActivity;
import com.wei.music.adapter.MyRecycleAdapter;
import com.wei.music.bean.SongListBean;
import com.wei.music.bean.SubCountBean;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.network.NestedApi;
import com.wei.music.network.NestedService;
import com.wei.music.utils.AudioFileFetcher;
import com.wei.music.utils.GlideLoadUtils;
import com.wei.music.utils.MMKVUtils;
import com.wei.music.utils.Resource;
import com.wei.music.utils.RxSchedulers;

import java.util.ArrayList;
import java.util.List;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private View mRootview, mDialogView;
    private BottomSheetDialog mLoginDialog;
    private RecyclerView mRecyclerview;
    private ImageView mUserImag, mUserBackImg;
    private TextView mUserName, mUserSignature;
    private MyRecycleAdapter mSonglistadapter;
    private List<SongListBean> mSonglist = new ArrayList<>();

    private HomeViewModel homeViewModel;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.home_fragment, container);

        initView();
        InitData();
        return mRootview;
    }

    private void observerData() {
        progressDialog = new ProgressDialog(requireContext());
        homeViewModel.captchaLiveData.observe(getViewLifecycleOwner(), new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> booleanResource) {
                if (booleanResource instanceof Resource.Error) {
                    Toast.makeText(requireContext(), "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (booleanResource instanceof Resource.Loading) {
                    progressDialog.show();
                } else if (booleanResource instanceof Resource.Success) {
                    Toast.makeText(requireContext(), "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
        AppSessionManager.Holder.instance.userLoginLiveData.observe(getViewLifecycleOwner(), new Observer<Resource<UserLoginBean>>() {
            @Override
            public void onChanged(Resource<UserLoginBean> userLoginBeanResource) {
                if (userLoginBeanResource instanceof Resource.Success) {
                    progressDialog.dismiss();
                    //todo
                    initUser(userLoginBeanResource.getData());
                    getUserSongs(userLoginBeanResource.getData().getProfile().getUserId());
                } else if (userLoginBeanResource instanceof Resource.Loading) {
                    progressDialog.show();
                } else if (userLoginBeanResource instanceof Resource.Error) {
                    Toast.makeText(requireContext(), userLoginBeanResource.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    /**
     * response 404
     * @param uid userId
     */
    private void getUserSongs(int uid) {
        Disposable subscribe = NestedService.ServiceHolder.service
                .getApi()
                .getPlayList(uid)
                .compose(RxSchedulers.applySchedulers())
                .onErrorReturn(new Function<Throwable, String>() {
                    @Override
                    public String apply(Throwable throwable) throws Throwable {
                        return "";
                    }
                })
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(getViewLifecycleOwner())))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Throwable {

                    }
                });
    }

    private void initUser(UserLoginBean data) {
        GlideLoadUtils.setCircle(requireContext(), data.getProfile().getAvatarUrl(), mUserImag);
        mUserName.setText(data.getProfile().getNickname());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        observerData();
    }

    private void initView() {
        mUserImag = (ImageView) mRootview.findViewById(R.id.user_icon);
        mUserImag.setOnClickListener(this);
        mUserBackImg = (ImageView) mRootview.findViewById(R.id.user_background);
        mUserName = (TextView) mRootview.findViewById(R.id.user_name);
        mUserSignature = (TextView) mRootview.findViewById(R.id.user_signature);
        mRecyclerview = (RecyclerView) mRootview.findViewById(R.id.recycleview_home);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setSmoothScrollbarEnabled(true);
        manager.setAutoMeasureEnabled(true);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(manager);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setNestedScrollingEnabled(false);
        mSonglistadapter = new MyRecycleAdapter(getActivity(), mSonglist);
        mRecyclerview.setAdapter(mSonglistadapter);
        mSonglistadapter.OnClickListener(new MyRecycleAdapter.OnItemClick() {
            @Override
            public void OnClick(SongListBean data, View image, View title, View msg) {
                MMKVUtils.putString("SongListId", data.getId());
                MMKVUtils.putString("SongListName", data.getTitle());
                MMKVUtils.putString("SongListIcon", data.getImage());
                View playBar = requireActivity().findViewById(R.id.playbar_view);
                Intent intent = new Intent(getActivity(), MusicListActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
                        Pair.create(image, "song_image"),
                        Pair.create(title, "song_title"),
                        Pair.create(msg, "song_msg"),
                        Pair.create(playBar, "song_playbar")).toBundle();
                startActivity(intent, bundle);
            }
        });
    }

    private void InitData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AudioFileFetcher.AudioFile> audioFiles = AudioFileFetcher.getAudioFiles(requireContext());
                if (!audioFiles.isEmpty()) {
                    mSonglist.add(0, AudioFileFetcher.cachedLocalSongs);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSonglistadapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user_icon) {
            login();
        }
    }

    public void login() {
        mDialogView = getLayoutInflater().inflate(R.layout.layout_login_captcha, null);
        mLoginDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle);
        mLoginDialog.setContentView(mDialogView);
        mLoginDialog.show();
        final EditText user = (EditText) mDialogView.findViewById(R.id.login_user);
        final EditText password = (EditText) mDialogView.findViewById(R.id.login_password);
        final Button login = (Button) mDialogView.findViewById(R.id.login_go);
        final Button btRequestCaptcha = mDialogView.findViewById(R.id.bt_request_captcha);
        btRequestCaptcha.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = user.getText().toString();
                homeViewModel.requestCaptcha(phone);
            }
        });
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSessionManager.Holder.instance.login(user.getText().toString(), password.getText().toString());
            }
        });
    }


    private void showLoginResult(String title, String msg) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

}

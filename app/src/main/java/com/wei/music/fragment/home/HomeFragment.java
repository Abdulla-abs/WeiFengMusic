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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.wei.music.AppSessionManager;
import com.wei.music.R;
import com.wei.music.activity.MusicListActivity;
import com.wei.music.adapter.MyRecycleAdapter;
import com.wei.music.bean.SongListBean;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.databinding.HomeFragmentBinding;
import com.wei.music.databinding.LayoutLoginCaptchaBinding;
import com.wei.music.network.NestedService;
import com.wei.music.utils.MMKVUtils;
import com.wei.music.utils.Resource;
import com.wei.music.utils.RxSchedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.functions.Consumer;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private HomeViewModel homeViewModel;
    private ProgressDialog progressDialog;
    private com.wei.music.databinding.HomeFragmentBinding binding;
    private MyRecycleAdapter mSongListAdapter;
    private BottomSheetDialog mLoginDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        progressDialog = new ProgressDialog(requireContext());
        binding.recycleviewHome.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        mSongListAdapter = new MyRecycleAdapter();
        binding.recycleviewHome.setAdapter(mSongListAdapter);
        mSongListAdapter.setListener(new MyRecycleAdapter.OnItemClickListener() {
            @Override
            public void onUserCardClick(UserLoginBean user) {
                Optional.ofNullable(user)
                        .ifPresentOrElse(new java.util.function.Consumer<UserLoginBean>() {
                            @Override
                            public void accept(UserLoginBean userLoginBean) {
                                //authed
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                login();
                            }
                        });
            }

            @Override
            public void onSongListClick(SongListBean data, View image, View title, View msg) {
                MMKVUtils.putString("SongListId", data.getId());
                MMKVUtils.putString("SongListName", data.getTitle());
                MMKVUtils.putString("SongListIcon", data.getImage());
                View playBar = requireActivity().findViewById(R.id.playbar_view);
                Intent intent = new Intent(getActivity(), MusicListActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        Pair.create(image, "song_image"),
                        Pair.create(title, "song_title"),
                        Pair.create(msg, "song_msg"),
                        Pair.create(playBar, "song_playbar")).toBundle();
                startActivity(intent, bundle);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        observerData();
    }

    private void initData() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.prepareSongListData(requireContext().getApplicationContext());
    }

    private void observerData() {
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
        homeViewModel.songList.observe(getViewLifecycleOwner(), new Observer<List<SongListBean>>() {
            @Override
            public void onChanged(List<SongListBean> songListBeans) {
                mSongListAdapter.submitList(new ArrayList<>(songListBeans));
            }
        });
        AppSessionManager.Holder.instance.userLoginLiveData.observe(getViewLifecycleOwner(), new Observer<Resource<UserLoginBean>>() {
            @Override
            public void onChanged(Resource<UserLoginBean> userLoginBeanResource) {
                if (userLoginBeanResource instanceof Resource.Success) {
                    progressDialog.dismiss();
                    mLoginDialog.dismiss();
                    initUser(userLoginBeanResource.getData());
                } else if (userLoginBeanResource instanceof Resource.Loading) {
                    progressDialog.show();
                } else if (userLoginBeanResource instanceof Resource.Error) {
                    Toast.makeText(requireContext(), userLoginBeanResource.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void initUser(UserLoginBean data) {
        mSongListAdapter.initUserData(data);
    }

    public void login() {
        LayoutLoginCaptchaBinding loginCaptchaBinding = LayoutLoginCaptchaBinding.inflate(LayoutInflater.from(requireContext()), null, false);
        mLoginDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle);
        mLoginDialog.setContentView(loginCaptchaBinding.getRoot());

        loginCaptchaBinding.btRequestCaptcha.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = loginCaptchaBinding.loginUser.getText().toString();
                homeViewModel.requestCaptcha(phone);
            }
        });
        loginCaptchaBinding.loginGo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSessionManager.Holder.instance.login(
                        loginCaptchaBinding.loginUser.getText().toString(),
                        loginCaptchaBinding.loginPassword.getText().toString()
                );
            }
        });
        mLoginDialog.show();
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

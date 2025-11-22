package com.wei.music.activity.play;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wei.music.R;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wei.music.mapper.MediaMetadataInfo;
import com.wei.music.mapper.MediaMetadataMapper;
import com.wei.music.view.LrcView;
import com.wei.music.utils.ColorUtil;
import com.wei.music.utils.CloudMusicApi;
import com.wei.music.utils.ToolUtil;
import com.wei.music.utils.GlideLoadUtils;

import android.graphics.Bitmap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlayerLrcFragment extends Fragment implements PlayerActivity.OnLrcListener {

    private View mRootView;
    private LrcView mLrcView;
    private PlayerActivity mActivity;

    PlayViewModel playViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.player_lrc_fragment, container);
        mActivity = (PlayerActivity) getActivity();
        mLrcView = mRootView.findViewById(R.id.player_lrcview);
        mLrcView.setDraggable(true, new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                mActivity.seekTo(time);
                return true;
            }
        });
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playViewModel  = new ViewModelProvider(this).get(PlayViewModel.class);
        initData();
    }

    private void initData() {
        playViewModel.mediaMetadataCompatMutableLiveData.observe(getViewLifecycleOwner(), new Observer<MediaMetadataCompat>() {
            @Override
            public void onChanged(MediaMetadataCompat mediaMetadataCompat) {
                MediaMetadataInfo info = MediaMetadataMapper.mapper(mediaMetadataCompat);
                onUpLrc(CloudMusicApi.MUSIC_LRC + info.getMediaId());
                GlideLoadUtils.loadBitmap(
                        requireContext(),
                        info.getAlbum(),
                        300,   // 高斯模糊强度
                        new CustomTarget<Bitmap>() {  // ← 改成 CustomTarget

                            @RequiresApi(api = Build.VERSION_CODES.Q)
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource,
                                                        @Nullable Transition<? super Bitmap> transition) {
                                onUpColor(ColorUtil.getColor(resource));
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // 可选：资源被清除时执行（比如 Activity destroy）
                            }
                        }
                );
            }
        });

    }

    @Override
    public void onUpLrc(String url) {
        mLrcView.loadLrcByUrl(url);
    }

    @Override
    public void onUpTime(long time) {
        mLrcView.updateTime(time);
    }

    @Override
    public void onUpColor(int[] colors) {
        mLrcView.setNormalColor(ColorUtil.getMixedColor(colors[0], colors[1]));
        mLrcView.setCurrentColor(colors[1]);
        mLrcView.setTimelineColor(colors[1]);
        mLrcView.setTimelineTextColor(colors[1]);
        mLrcView.setTimeTextColor(colors[1]);
        mLrcView.setPlayDrawableColor(colors[1]);
    }

}

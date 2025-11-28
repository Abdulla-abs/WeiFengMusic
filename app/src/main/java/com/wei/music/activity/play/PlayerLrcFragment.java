package com.wei.music.activity.play;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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

import com.wei.music.di.NetWorkModule;
import com.wei.music.mapper.MediaMetadataInfo;
import com.wei.music.mapper.MediaMetadataMapper;
import com.wei.music.service.controller.MusicController;
import com.wei.music.view.LrcView;
import com.wei.music.utils.ColorUtil;
import com.wei.music.utils.CloudMusicApi;
import com.wei.music.utils.ToolUtil;
import com.wei.music.utils.GlideLoadUtils;

import android.graphics.Bitmap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlayerLrcFragment extends Fragment {

    private View mRootView;
    private LrcView mLrcView;
    private PlayerActivity mActivity;

    PlayViewModel playViewModel;

    @Inject
    MusicController musicController;

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

        playViewModel = new ViewModelProvider(this).get(PlayViewModel.class);
        initMusicController();
        initData();
    }

    private void initMusicController() {
        musicController.registerControllerCallback(callback);
        initMetaData(musicController.getMediaControllerCompat().getMetadata());
    }

    private void initData() {

    }

    private final MediaControllerCompat.Callback callback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            onUpTime(state.getPosition());
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            initMetaData(metadata);
        }
    };

    private void initMetaData(MediaMetadataCompat metadata){
        MediaMetadataInfo music = MediaMetadataMapper.mapper(metadata);
        if (music == null) return;
        onUpLrc(NetWorkModule.NESTED_BASE_URL + "lyric?id=" + music.getMediaId());
        GlideLoadUtils.loadBitmap(
                requireContext(),
                music.getAlbum(),
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

    private void onUpLrc(String url) {
        mLrcView.loadLrcByUrl(url);
    }


    private void onUpTime(long time) {
        mLrcView.updateTime(time);
    }


    private void onUpColor(int[] colors) {
        mLrcView.setNormalColor(ColorUtil.getMixedColor(colors[0], colors[1]));
        mLrcView.setCurrentColor(colors[1]);
        mLrcView.setTimelineColor(colors[1]);
        mLrcView.setTimelineTextColor(colors[1]);
        mLrcView.setTimeTextColor(colors[1]);
        mLrcView.setPlayDrawableColor(colors[1]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicController.unregisterControllerCallback(callback);
    }
}

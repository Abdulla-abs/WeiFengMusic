package com.wei.music.activity.play;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wei.music.R;
import com.wei.music.mapper.MediaMetadataInfo;
import com.wei.music.mapper.MediaMetadataMapper;
import com.wei.music.service.MusicService;
import com.wei.music.service.controller.MusicController;
import com.wei.music.utils.ColorUtil;
import com.wei.music.utils.GlideLoadUtils;
import com.wei.music.utils.ToolUtil;
import com.wei.music.view.VisualizerView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlayerVisualizerFragment extends Fragment {

    private View mRootView;
    private VisualizerView mVisualizer;
    private ImageView mPlayerImage;
    private boolean isVertical;

    PlayViewModel playViewModel;

    @Inject
    MusicController musicController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.player_visualizer_fragment, container);
        isVertical = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        initView();
        initData();
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMusicController();
        initMediaMetaData(musicController.getMediaControllerCompat().getMetadata());
    }

    private void initMusicController() {
        musicController.registerControllerCallback(callback);
    }

    private void initView() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int widthPixels = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
        mVisualizer = mRootView.findViewById(R.id.player_visualizer);
        mVisualizer.setAudioSessionId(MusicService.FIXED_AUDIO_SESSION_ID);
        mPlayerImage = mRootView.findViewById(R.id.player_image);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPlayerImage.getLayoutParams();
        params.width = widthPixels / 2 - ToolUtil.dip2px(getActivity(), 34);
        params.height = widthPixels / 2 - ToolUtil.dip2px(getActivity(), 34);
        if (!isVertical) {
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            mVisualizer.setShowStyle(VisualizerView.ShowStyle.STYLE_LINE_BAR_AND_WAVE);
        }
        mPlayerImage.setLayoutParams(params);
    }

    private void initData() {
        playViewModel = new ViewModelProvider(this).get(PlayViewModel.class);

    }

    private final MediaControllerCompat.Callback callback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            initMediaMetaData(metadata);
        }
    };


    private void initMediaMetaData(MediaMetadataCompat metadata) {
        MediaMetadataInfo music = MediaMetadataMapper.mapper(metadata);
        if (music == null) return;
        onUpImage(music.getAlbum());
        GlideLoadUtils.loadBitmap(
                requireContext(),
                music.getAlbum(),
                300,   // 高斯模糊强度
                new CustomTarget<Bitmap>() {  // ← 改成 CustomTarget

                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        onUpColor(ColorUtil.getColor(resource)[1]);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // 可选：资源被清除时执行（比如 Activity destroy）
                    }
                }
        );
    }

    public void onUpImage(String url) {
        if (isVertical) {
            GlideLoadUtils.setCircle(requireContext(), url, mPlayerImage);
        } else {
            GlideLoadUtils.setRound(url, 8, mPlayerImage);
        }
    }

    public void onUpColor(int color) {
        mVisualizer.setLineBarColor(color);
    }

    @Override
    public void onResume() {
        super.onResume();
        mVisualizer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVisualizer.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVisualizer.release();
        musicController.unregisterControllerCallback(callback);
    }

}

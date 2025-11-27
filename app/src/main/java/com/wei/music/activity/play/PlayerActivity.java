package com.wei.music.activity.play;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateFormat;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wei.music.R;
import com.wei.music.activity.EqualizerActivity;
import com.wei.music.activity.MusicListDialog;
import com.wei.music.mapper.MediaMetadataInfo;
import com.wei.music.mapper.MediaMetadataMapper;
import com.wei.music.service.MusicService;
import com.wei.music.service.MusicServiceModeHelper;
import com.wei.music.utils.CloudMusicApi;
import com.wei.music.utils.ColorUtil;
import com.wei.music.utils.GlideLoadUtils;
import com.wei.music.utils.ToolUtil;
import com.wei.music.view.FinishLayout;

import java.util.List;

import android.os.Build;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import com.wei.music.adapter.MainPagerAdapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wei.music.view.MarqueeView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, FinishLayout.OnFinishListener {

    private FrameLayout mLrcFrameLayout, mVisualizerFrameLayout;
    private ViewPager2 mViewPager2;
    private Fragment mLrcFragment, mVisualizerFragment;
    private List<Fragment> mPagerFragments = new ArrayList<>();
    private FinishLayout mFinishLayout;
    private MarqueeView mPlayerTitle;
    private TextView mPlayerSinger, mPlayerStartText, mPlayerEndText;
    private ImageView mPlayerLike, mPlayerComment, mPlayerEqualizer, mPlayerMore, mPlayerBack, mPlayerPrevious, mPlayerPlay, mPlayerNext, mPlayerList, mPlayerModel;
    private SeekBar mPlayerSeekBar;

    private MediaBrowserCompat mMediaBrowser;
    private MediaControllerCompat mMediaController;

    private String mMusicId = "";

    private boolean isVertical;

    private OnLrcListener mLrcListener;
    private OnVisualizerListener mVisualizerListener;

    private FragmentManager mFragmentManager;

    private FragmentTransaction mFragmentTransaction;

    private PlayViewModel viewModel;

    public interface OnLrcListener {
        void onUpLrc(String url);

        void onUpTime(long time);

        void onUpColor(int[] colors);
    }

    public interface OnVisualizerListener {
        void onUpImage(String url);

        void onUpColor(int color);
    }

    public void seekTo(long time) {
        mMediaController.getTransportControls().seekTo(time);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment == mLrcFragment)
            mLrcListener = (OnLrcListener) fragment;
        if (fragment == mVisualizerFragment)
            mVisualizerListener = (OnVisualizerListener) fragment;
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ToolUtil.setStatusBarColor(this, Color.TRANSPARENT, Color.TRANSPARENT, true);
        isVertical = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);

        viewModel = new ViewModelProvider(this).get(PlayViewModel.class);
        initView();
        initMediaBrowser();
    }

    private void initMediaBrowser() {
        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class),
                connectionCallback, null);
        mMediaBrowser.connect();
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();
            mMediaController = new MediaControllerCompat(PlayerActivity.this, token);
            MediaMetadataCompat metadata = mMediaController.getMetadata();
            initMediaMetaData(metadata);
            mMediaController.registerCallback(mMediaCallback);
            mMediaBrowser.subscribe(MusicService.MUSIC_FAVORITE, mCallback);
        }
    };

    private final MediaControllerCompat.Callback mMediaCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            super.onRepeatModeChanged(repeatMode);
            upModelView();
        }

        @Override
        public void onShuffleModeChanged(int shuffleMode) {
            super.onShuffleModeChanged(shuffleMode);
            upModelView();
        }

        @Override
        public void onExtrasChanged(Bundle extras) {
            super.onExtrasChanged(extras);
            upModelView();
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            MediaDescriptionCompat description = metadata.getDescription();
            if (!mMusicId.equals(description.getMediaId())) {
                initMediaMetaData(metadata);
            }
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            mPlayerSeekBar.setProgress((int) state.getPosition());
            upMusicView(state);
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }
    };

    private MediaBrowserCompat.SubscriptionCallback mCallback = new MediaBrowserCompat.SubscriptionCallback() {

        @Override
        public void onChildrenLoaded(String parentId, List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            if (parentId.equals(MusicService.MUSIC_FAVORITE)) {
//                for (int i = 0; i < children.size(); i++) {
//                    if (children.get(i).getDescription().getMediaId().equals(ToolUtil.readString("MusicId"))) {
//                        mPlayerLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_fill));
//                        isLike = true;
//                        return;
//                    }
//                }
            }
        }

        @Override
        public void onError(String parentId) {
            super.onError(parentId);
        }
    };

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.player_like) {

//            if (isLike) {
//                mPlayerLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline));
//                isLike = false;
//            } else {
//                mPlayerLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_fill));
//                isLike = true;
//            }
//            Bundle likebundle = new Bundle();
//            likebundle.putString("id", ToolUtil.readString("MusicId"));
//            likebundle.putBoolean("is", isLike);
//            mMediaController.getTransportControls().sendCustomAction(MusicService.ACTION_LIKE_MUSIC, likebundle);
        } else if (viewId == R.id.player_equalizer) {
            startActivity(new Intent(this, EqualizerActivity.class));
        } else if (viewId == R.id.player_previous) {
            mMediaController.getTransportControls().skipToPrevious();
        } else if (viewId == R.id.player_play) {
            mMediaController.getTransportControls().play();
        } else if (viewId == R.id.player_next) {
            mMediaController.getTransportControls().skipToNext();
        } else if (viewId == R.id.player_list) {
            startActivity(new Intent(this, MusicListDialog.class));
        } else if (viewId == R.id.player_model) {
            MusicServiceModeHelper.toggleMode(mMediaController.getTransportControls());
        }
    }

    private void upModelView() {
        switch (MusicServiceModeHelper.getCurrentMode()) {
            case SINGLE_CIRCLE:
                mPlayerModel.setImageDrawable(getResources().getDrawable(R.drawable.ic_single));
                break;
            case SHUFFLE_CIRCLE:
                mPlayerModel.setImageDrawable(getResources().getDrawable(R.drawable.ic_random));
                break;
            case LIST_CIRCLE:
                mPlayerModel.setImageDrawable(getResources().getDrawable(R.drawable.ic_sequence));
                break;
        }
    }

    private void upMusicView(PlaybackStateCompat state) {
        mPlayerPlay.setImageDrawable((state.getState() == PlaybackStateCompat.STATE_PLAYING) ? getResources().getDrawable(R.drawable.ic_play) : getResources().getDrawable(R.drawable.ic_pause));
        mPlayerStartText.setText(ToolUtil.getTime("mm:ss", state.getPosition()));
        if (mLrcListener != null) {
            mLrcListener.onUpTime(state.getPosition());
        }
    }

    private void initView() {
        mLrcFragment = new PlayerLrcFragment();
        mVisualizerFragment = new PlayerVisualizerFragment();
        if (isVertical) {
            mViewPager2 = (ViewPager2) findViewById(R.id.view_pager_player);
            mPagerFragments.add(mVisualizerFragment);
            mPagerFragments.add(mLrcFragment);
            mViewPager2.setAdapter(new MainPagerAdapter(this, mPagerFragments));
        } else {
            mLrcFrameLayout = (FrameLayout) findViewById(R.id.player_lrc_fragment);
            mVisualizerFrameLayout = (FrameLayout) findViewById(R.id.player_visualizer_fragment);
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.add(R.id.player_lrc_fragment, mLrcFragment);
            mFragmentTransaction.add(R.id.player_visualizer_fragment, mVisualizerFragment);
            mFragmentTransaction.commit();
        }
        mFinishLayout = (FinishLayout) findViewById(R.id.slidingLayout);
        mFinishLayout.setOnFinishListener(this);
        mPlayerTitle = (MarqueeView) findViewById(R.id.player_title);
        mPlayerSinger = (TextView) findViewById(R.id.player_singer);
        mPlayerBack = (ImageView) findViewById(R.id.player_back);
        mPlayerSeekBar = (SeekBar) findViewById(R.id.player_seekbar);
        mPlayerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int p2, boolean p3) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
                seekTo(seekbar.getProgress());
            }
        });
        mPlayerStartText = (TextView) findViewById(R.id.player_starttext);
        mPlayerEndText = (TextView) findViewById(R.id.player_endtext);
        mPlayerLike = (ImageView) findViewById(R.id.player_like);
        mPlayerLike.setOnClickListener(this);
        mPlayerComment = (ImageView) findViewById(R.id.player_comment);
        mPlayerEqualizer = (ImageView) findViewById(R.id.player_equalizer);
        mPlayerEqualizer.setOnClickListener(this);
        mPlayerMore = (ImageView) findViewById(R.id.player_more);
        mPlayerPrevious = (ImageView) findViewById(R.id.player_previous);
        mPlayerPrevious.setOnClickListener(this);
        mPlayerPlay = (ImageView) findViewById(R.id.player_play);
        mPlayerPlay.setOnClickListener(this);
        mPlayerNext = (ImageView) findViewById(R.id.player_next);
        mPlayerNext.setOnClickListener(this);
        mPlayerList = (ImageView) findViewById(R.id.player_list);
        mPlayerList.setOnClickListener(this);
        mPlayerModel = (ImageView) findViewById(R.id.player_model);
        mPlayerModel.setOnClickListener(this);
    }

    private void initMediaMetaData(MediaMetadataCompat metadata) {
        if (metadata == null) {
            viewModel.mediaMetadataCompatMutableLiveData.postValue(null);
            return;
        }
        viewModel.mediaMetadataCompatMutableLiveData.postValue(metadata);

        MediaMetadataInfo music = MediaMetadataMapper.mapper(metadata);

        upModelView();
        mPlayerLike.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_heart_outline, getTheme()));
        mMusicId = music.getMediaId();
        mPlayerSeekBar.setMax((int) music.getDuration());
        CharSequence sysTimeStr = DateFormat.format("mm:ss", music.getDuration());
        mPlayerEndText.setText(sysTimeStr);
        mPlayerTitle.setText(music.getTitle());
        mPlayerSinger.setText(music.getArtist());
        GlideLoadUtils.loadBitmap(
                PlayerActivity.this,
                music.getAlbum(),
                300,   // 高斯模糊强度
                new CustomTarget<Bitmap>() {  // ← 改成 CustomTarget

                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        initDataView(resource);  // 完全保持你原来的逻辑不动
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // 可选：资源被清除时执行（比如 Activity destroy）
                    }
                }
        );
        if (mVisualizerListener != null)
            mVisualizerListener.onUpImage(music.getAlbum());
        if (mLrcListener != null)
            mLrcListener.onUpLrc(CloudMusicApi.MUSIC_LRC + music.getMediaId());

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void initDataView(Bitmap bitmap) {
        int[] colors = ColorUtil.getColor(bitmap);
        mPlayerBack.setImageBitmap(bitmap);
        ToolUtil.setStatusBarTextColor(PlayerActivity.this, colors[0]);
        mPlayerTitle.setTextColor(colors[1]);
        mPlayerSinger.setTextColor(colors[1]);
        mPlayerStartText.setTextColor(colors[1]);
        mPlayerEndText.setTextColor(colors[1]);
        mPlayerLike.setColorFilter(colors[1]);
        mPlayerComment.setColorFilter(colors[1]);
        mPlayerEqualizer.setColorFilter(colors[1]);
        mPlayerMore.setColorFilter(colors[1]);
        mPlayerPrevious.setColorFilter(colors[1]);
        mPlayerPlay.setColorFilter(colors[1]);
        mPlayerNext.setColorFilter(colors[1]);
        mPlayerList.setColorFilter(colors[1]);
        mPlayerModel.setColorFilter(colors[1]);
        if (mLrcListener != null)
            mLrcListener.onUpColor(colors);
        if (mVisualizerListener != null)
            mVisualizerListener.onUpColor(colors[1]);
        LayerDrawable layerDrawable = (LayerDrawable) mPlayerSeekBar.getProgressDrawable();
        Drawable drawable = layerDrawable.getDrawable(2);
        drawable.setColorFilter(colors[1], PorterDuff.Mode.SRC);
        StateListDrawable statelist = (StateListDrawable) mPlayerSeekBar.getThumb();
        statelist.getStateDrawable(0).setColorFilter(colors[1], PorterDuff.Mode.SRC);
        statelist.getStateDrawable(1).setColorFilter(colors[1], PorterDuff.Mode.SRC);
        statelist.getStateDrawable(2).setColorFilter(colors[1], PorterDuff.Mode.SRC);
        mPlayerSeekBar.setThumb(statelist);
        mPlayerSeekBar.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onFinish() {
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("android:support:fragments", null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mMediaCallback);
            mMediaController = null;
        }
        if (mMediaBrowser.isConnected()) {
            mMediaBrowser.unsubscribe(MusicService.MUSIC_FAVORITE);
            mMediaBrowser.disconnect();
        }
    }
}

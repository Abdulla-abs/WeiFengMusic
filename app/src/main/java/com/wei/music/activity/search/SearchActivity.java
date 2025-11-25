package com.wei.music.activity.search;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wei.music.R;
import com.wei.music.activity.MusicListDialog;
import com.wei.music.activity.play.PlayerActivity;
import com.wei.music.adapter.SearchPagerAdapter;
import com.wei.music.databinding.ActivitySearchBinding;
import com.wei.music.mapper.MediaMetadataInfo;
import com.wei.music.mapper.MediaMetadataMapper;
import com.wei.music.service.MusicService;
import com.wei.music.service.util.MediaControllerHelper;
import com.wei.music.utils.ColorUtil;
import com.wei.music.utils.GlideLoadUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private SearchViewModel viewModel;
    private MediaBrowserCompat mediaBrowserCompat;
    private MediaControllerCompat mediaControllerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initMediaBrowser();
        initData();
        observerData();
    }


    private void initView() {
        binding.viewpager.setUserInputEnabled(false);
        binding.viewpager.setAdapter(new SearchPagerAdapter(this));
        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = binding.searchEdittext.getText().toString();
                viewModel.searchIntentMutableLiveData.postValue(
                        new SearchIntent.Search(content)
                );
            }
        });

        binding.playBar.playbarPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MediaControllerHelper(mediaControllerCompat).togglePlayPause();
            }
        });
        binding.playBar.playbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this, PlayerActivity.class));
            }
        });
        binding.playBar.playbarList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this, MusicListDialog.class));
            }
        });
    }

    private void initMediaBrowser() {
        mediaBrowserCompat = new MediaBrowserCompat(
                this,
                new ComponentName(this, MusicService.class),
                getMediaConnectionCallback(),
                null
        );
        mediaBrowserCompat.connect();
    }

    private void initData() {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.viewpager.getCurrentItem() != 0) {
                    binding.viewpager.setCurrentItem(0, false);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void observerData() {
        viewModel.searchIntentMutableLiveData.observe(this, new Observer<SearchIntent>() {
            @Override
            public void onChanged(SearchIntent searchIntent) {
                if (searchIntent instanceof SearchIntent.Search) {
                    binding.viewpager.setCurrentItem(1, false);
                    viewModel.storeSearchKeywords((SearchIntent.Search) searchIntent);
                }
            }
        });
    }

    private MediaBrowserCompat.ConnectionCallback connectionCallback;

    private MediaBrowserCompat.ConnectionCallback getMediaConnectionCallback() {
        if (connectionCallback == null) {
            connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    MediaSessionCompat.Token token = mediaBrowserCompat.getSessionToken();
                    mediaControllerCompat = new MediaControllerCompat(SearchActivity.this, token);
                    mediaControllerCompat.registerCallback(getMediaControllerCallback());
                }
            };
        }
        return connectionCallback;
    }

    private MediaControllerCompat.Callback callback = null;

    private MediaControllerCompat.Callback getMediaControllerCallback() {
        if (callback == null) {
            callback = new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                    onMetadataChange(metadata);
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    binding.playBar.playbarPause.setImageDrawable((state.getState() == PlaybackStateCompat.STATE_PLAYING) ?
                            ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, getTheme()) :
                            ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, getTheme())
                    );
                }
            };
        }
        return callback;
    }

    private void onMetadataChange(MediaMetadataCompat metadata) {
        MediaMetadataInfo info = MediaMetadataMapper.mapper(metadata);

        GlideLoadUtils.setCircle(this, info.getAlbum(), binding.playBar.playbarIcon);
        binding.playBar.playbarTitle.setText(info.getTitle() + "-" + info.getArtist());
        Glide.with(this)
                .asBitmap()
                .load(info.getAlbum())
                .into(new CustomTarget<Bitmap>() {  // ← 改成 CustomTarget！！！

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        // 这里就是原来的 onResourceReady 内容
                        int[] colors = ColorUtil.getColor(resource);
                        GradientDrawable mGroupDrawable = (GradientDrawable) binding.playBar.playbarRoot.getBackground();
                        mGroupDrawable.setColor(colors[1]);
                        binding.playBar.playbarTitle.setTextColor(colors[0]);
                        binding.playBar.playbarPause.setColorFilter(colors[0]);
                        binding.playBar.playbarList.setColorFilter(colors[0]);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // 可选：资源被清除时调用（比如 detach 时）
                    }
                });

    }
}

package com.wei.music.activity.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.wei.music.MusicSessionManager;
import com.wei.music.R;
import com.wei.music.activity.MusicListDialog;
import com.wei.music.activity.play.PlayerActivity;
import com.wei.music.activity.search.SearchActivity;
import com.wei.music.activity.about.AboutFragment;
import com.wei.music.databinding.ActivityMainBinding;
import com.wei.music.fragment.home.HomeFragment;
import com.wei.music.fragment.MoreFragment;
import com.wei.music.mapper.MediaMetadataInfo;
import com.wei.music.mapper.MediaMetadataMapper;
import com.wei.music.service.controller.MusicController;
import com.wei.music.utils.ColorUtil;
import com.wei.music.utils.GlideLoadUtils;
import com.wei.music.utils.PlayBarTransformer;
import com.wei.music.utils.ToolUtil;
import com.wei.music.view.MainPagerCallback;

import java.util.Arrays;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;

import com.wei.music.adapter.MainPagerAdapter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener {

    private MainActivityViewModel mainViewModel;

    @Inject
    MusicSessionManager musicSessionManager;
    @Inject
    MusicController controller;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolUtil.setStatusBarColor(this, Color.TRANSPARENT, getResources().getColor(R.color.colorPrimary), true);

        mainViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainViewModel.init();

        initView();
        initData();
    }

    private void initData() {
        controller.registerControllerCallback(mMediaCallback);
    }

    private final MediaControllerCompat.Callback mMediaCallback = new MediaControllerCompat.Callback() {

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

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }
    };

    private void initView() {
        List<Fragment> pagers = Arrays.asList(new HomeFragment(), new MoreFragment(), new AboutFragment());
        MainPagerAdapter mPagerAdapter = new MainPagerAdapter(this, pagers);
        binding.viewPagerMain.setAdapter(mPagerAdapter);
        binding.viewPagerMain.registerOnPageChangeCallback(new MainPagerCallback(binding.toolbar.toolbarTitle));
        binding.leftNav.setNavigationItemSelectedListener(this);
        binding.leftNav.getMenu().getItem(0).setChecked(true);
        binding.leftNav.setBackgroundColor(Color.TRANSPARENT);
        binding.drawerMain.addDrawerListener(this);
        binding.drawerMain.setScrimColor(Color.TRANSPARENT);
        binding.toolbar.toolbarBut.setOnClickListener(this);
        binding.playBar.playbarList.setOnClickListener(this);
        binding.playBar.playbarPause.setOnClickListener(this);
        binding.playBar.playbarView.setOnClickListener(this);
    }

    private void onMetadataChange(MediaMetadataCompat metadata) {
        MediaMetadataInfo info = MediaMetadataMapper.mapper(metadata);

        GlideLoadUtils.setCircle(this, info.getAlbum(), binding.playBar.playbarIcon);
        binding.playBar.playbarTitle.setText(info.getTitle() + "-" + info.getArtist());
        Glide.with(this)
                .asBitmap()
                .load(info.getAlbum())
                .into(new PlayBarTransformer(binding.playBar));

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playbar_list) {
            startActivity(new Intent(this, MusicListDialog.class));
        } else if (v.getId() == R.id.playbar_pause) {
            controller.getMediaControllerCompat().getTransportControls().play();
        } else if (v.getId() == R.id.play_bar) {
            startActivity(new Intent(MainActivity.this, PlayerActivity.class));
        } else if (v.getId() == R.id.toolbar_but) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.toolbar.toolbarBut, "search");
            startActivity(new Intent(this, SearchActivity.class), options.toBundle());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        return true;
    }

    @Override
    public void onDrawerSlide(View view, float slideOffset) {
        float scale = 1 - slideOffset;//1~0
        float leftScale = (float) (1 - 0.3 * scale);
        float rightScale = (float) (0.7f + 0.3 * scale);//0.7~1
        view.setScaleX(leftScale);//1~0.7
        view.setScaleY(leftScale);//1~0.7

        binding.contCard.setScaleX(rightScale);
        binding.contCard.setScaleY(rightScale);
        binding.contCard.setTranslationX(view.getMeasuredWidth() * slideOffset);//0~width
        binding.contCard.setRadius(ToolUtil.dip2px(this, 18 * slideOffset));
        binding.contCard.setElevation(ToolUtil.dip2px(this, 4 * slideOffset));
    }

    @Override
    public void onDrawerOpened(@NonNull View p1) {
    }

    @Override
    public void onDrawerClosed(@NonNull View p1) {
    }

    @Override
    public void onDrawerStateChanged(int p1) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.drawerMain.isDrawerOpen(GravityCompat.START)) {
                binding.drawerMain.closeDrawers();
            } else {
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    //在活动销毁时对服务进行解绑和停止线程
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}


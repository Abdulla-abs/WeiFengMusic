package com.wei.music.activity.musiclist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import android.widget.FrameLayout;
import android.widget.Toast;

import com.wei.music.MusicSessionManager;
import com.wei.music.R;
import com.wei.music.activity.MusicListDialog;
import com.wei.music.activity.play.PlayerActivity;
import com.wei.music.adapter.MusicListAdapter;
import com.wei.music.bean.CreatorDTO;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.UserMusicListBean;
import com.wei.music.mapper.MediaMetadataInfo;
import com.wei.music.mapper.MediaMetadataMapper;
import com.wei.music.service.musicaction.MusicActionContract;
import com.wei.music.service.musicaction.MusicIntentContract;
import com.wei.music.utils.AudioFileFetcher;
import com.wei.music.utils.GlideLoadUtils;
import com.wei.music.utils.Resource;
import com.wei.music.utils.ToolUtil;
import com.wei.music.view.MarqueeView;
import com.wei.music.service.MusicService;
import com.wei.music.utils.ColorUtil;
import com.wei.music.utils.AppBarStateChangeListener;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.Disposable;


@AndroidEntryPoint
public class MusicListActivity extends AppCompatActivity implements View.OnClickListener, MusicListAdapter.OnItemClick {

    public static final String INTENT_SONG_LIST = "IntentSongList";
    private LinearLayout mPlayBarRoot;
    private FrameLayout mPlayBarView;
    private ImageView mMusicListBackground, mMusicListIcon, mPlayBarIcon, mPlayBarPause, mPlayBarList;
    private RecyclerView mRecyclerView;
    private MusicListAdapter musicListAdapter;
    private MarqueeView mTitleView;
    private TextView mMusicListName, mMusicListMsg, mPlayBarTitle;
    private AppBarLayout mAppBarLayout;
    private MediaBrowserCompat mMediaBrowser;
    private MediaControllerCompat mMediaController;

    private int[] colors = null;

    MusicListViewModel viewModel;

    @Inject
    MusicSessionManager musicSessionManager;
    private PlaylistDTO playlistDTO;
    private final int PERMISSION_REQUEST_CODE = 0X21475457;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);
        ToolUtil.setStatusBarColor(this, Color.TRANSPARENT, getResources().getColor(R.color.colorPrimary, getTheme()), true);

        viewModel = new ViewModelProvider(this).get(MusicListViewModel.class);

        initView();
        initMediaBrowser();
        initData();
    }

    public void initView() {
        mTitleView = (MarqueeView) findViewById(R.id.toolbar_title);
        mTitleView.setText("歌单");
        mAppBarLayout = (AppBarLayout) findViewById(R.id.musiclist_appbar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
                switch (state) {
                    case EXPANDED:
                        mTitleView.setText("");
                        resetSongListBackground();
                        break;
                    case COLLAPSED:
                        mTitleView.setText(mMusicListName.getText());
                        ToolUtil.setStatusBarTextColor(MusicListActivity.this,
                                getResources().getColor(R.color.colorPrimary, getTheme()));
                        break;
                    case INTERMEDIATE:
                        break;
                }
            }
        });
        mMusicListName = (TextView) findViewById(R.id.musiclist_name);
        mMusicListMsg = (TextView) findViewById(R.id.musiclist_msg);
        mMusicListBackground = (ImageView) findViewById(R.id.musiclist_back);
        mMusicListIcon = (ImageView) findViewById(R.id.musiclist_icon);
        mRecyclerView = (RecyclerView) findViewById(R.id.music_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mPlayBarIcon = (ImageView) findViewById(R.id.playbar_icon);
        mPlayBarList = (ImageView) findViewById(R.id.playbar_list);
        mPlayBarList.setOnClickListener(this);
        mPlayBarPause = (ImageView) findViewById(R.id.playbar_pause);
        mPlayBarTitle = (MarqueeView) findViewById(R.id.playbar_title);
        mPlayBarPause.setOnClickListener(this);
        mPlayBarRoot = (LinearLayout) findViewById(R.id.playbar_root);
        mPlayBarView = (FrameLayout) findViewById(R.id.playbar_view);
        mPlayBarView.setOnClickListener(this);
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
            mMediaController = new MediaControllerCompat(MusicListActivity.this, token);
            mMediaController.registerCallback(mMediaCallback);
            onMusicServiceConnected();
            onMusicMetaDataChange(mMediaController.getMetadata());
        }
    };

    private final MediaControllerCompat.Callback mMediaCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            onMusicMetaDataChange(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            mPlayBarPause.setImageDrawable(
                    (state.getState() == PlaybackStateCompat.STATE_PLAYING) ?
                            ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, getTheme()) :
                            ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pause, getTheme())
            );
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }
    };


    private void initData() {
        playlistDTO = GsonUtils.fromJson(getIntent().getStringExtra(INTENT_SONG_LIST),
                PlaylistDTO.class);

        viewModel.fetchPlayListDetail(playlistDTO);

        if (playlistDTO != null && playlistDTO.getId() == AudioFileFetcher.LOCAL_SONG_LIST_ID &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int result = ContextCompat.checkSelfPermission(MusicListActivity.this, Manifest.permission.READ_MEDIA_AUDIO);
            if (result != PackageManager.PERMISSION_GRANTED) {
                requestAudioPermission();
            }
        }


        viewModel.playListDetail.observe(this, new Observer<Resource<UserMusicListBean.PlayList>>() {
            @Override
            public void onChanged(Resource<UserMusicListBean.PlayList> playListResource) {

            }
        });
        viewModel.playListDetailQueue.observe(this, new Observer<List<MediaSessionCompat.QueueItem>>() {
            @Override
            public void onChanged(List<MediaSessionCompat.QueueItem> queueItems) {
                musicListAdapter.setQueueItems(queueItems);
            }
        });
        musicListAdapter = new MusicListAdapter(MusicListActivity.this);
        musicListAdapter.OnClickListener(MusicListActivity.this);
        mRecyclerView.setAdapter(musicListAdapter);
    }


    private void onMusicMetaDataChange(MediaMetadataCompat metadata) {
        if (metadata == null) return;
        MediaMetadataInfo music = MediaMetadataMapper.mapper(metadata);
        if (music == null) return;

        GlideLoadUtils.setCircle(this, music.getAlbum(), mPlayBarIcon);
        mPlayBarTitle.setText(music.getTitle() + "-" + music.getArtist());
        Glide.with(this)  // Activity 用 this 最安全，Hilt 完全兼容
                .asBitmap()
                .load(music.getAlbum())
                .into(new CustomTarget<Bitmap>() {  // ← 改成 CustomTarget
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        int[] colors = ColorUtil.getColor(resource);
                        GradientDrawable mGroupDrawable = (GradientDrawable) mPlayBarRoot.getBackground();
                        if (mGroupDrawable != null) {
                            mGroupDrawable.setColor(colors[1]);
                        }
                        mPlayBarTitle.setTextColor(colors[0]);
                        mPlayBarPause.setColorFilter(colors[0]);
                        mPlayBarList.setColorFilter(colors[0]);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // 可选：资源被清除时（比如 detach）执行清理
                    }
                });

    }

    public void onMusicServiceConnected() {
        mMusicListName.setText(playlistDTO.getName());
        GlideLoadUtils.setRound(playlistDTO.getCoverImgUrl(), 8, mMusicListIcon);
        resetSongListBackground();
    }

    public void resetSongListBackground() {
        Optional.ofNullable(playlistDTO)
                .map(new Function<PlaylistDTO, CreatorDTO>() {
                    @Override
                    public CreatorDTO apply(PlaylistDTO playlistDTO) {
                        return playlistDTO.getCreator();
                    }
                })
                .ifPresent(new Consumer<CreatorDTO>() {
                    @Override
                    public void accept(CreatorDTO creatorDTO) {
                        GlideLoadUtils.loadBitmap(
                                MusicListActivity.this,
                                creatorDTO.getBackgroundUrl(),
                                1,  // radiusDp = 0（不需要圆角）
                                300, // blurRadius = 300（高斯模糊强度）
                                new CustomTarget<Bitmap>() {

                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource,
                                                                @Nullable Transition<? super Bitmap> transition) {
                                        colors = ColorUtil.getColor(resource);
                                        mMusicListBackground.setImageBitmap(resource);
                                        mMusicListName.setTextColor(colors[1]);
                                        mMusicListMsg.setTextColor(colors[1]);
                                        ToolUtil.setStatusBarTextColor(MusicListActivity.this, colors[0]);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                }
                        );
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playbar_pause) {
            mMediaController.getTransportControls().play();
        } else if (v.getId() == R.id.playbar_view) {
            startActivity(new Intent(this, PlayerActivity.class));
        } else if (v.getId() == R.id.playbar_list) {
            startActivity(new Intent(this, MusicListDialog.class));
        }
    }

    @Override
    public void OnClick(MediaSessionCompat.QueueItem data, int position) {
        List<MediaSessionCompat.QueueItem> queueItems = viewModel.playListDetailQueue.getValue();
        if (queueItems == null || queueItems.isEmpty()) {
            Toast.makeText(MusicListActivity.this, "当前歌单列表没有歌曲", Toast.LENGTH_SHORT).show();
            return;
        }
        int index = queueItems.indexOf(data);
        if (index < 0) {
            Toast.makeText(MusicListActivity.this, "未找到指定歌曲", Toast.LENGTH_SHORT).show();
            return;
        }

        PlaybackStateCompat playbackState = mMediaController.getPlaybackState();
        musicSessionManager.onMusicIntent(
                new MusicIntentContract.ChangePlayListOrSkipToPosition(queueItems, position, playbackState)
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Disposable subscribe = musicSessionManager.loadLocalSongList()
                        .subscribe(new io.reactivex.rxjava3.functions.Consumer<List<PlaylistDTO>>() {
                            @Override
                            public void accept(List<PlaylistDTO> unused) throws Throwable {
                                if (!unused.isEmpty()){
                                    viewModel.fetchPlayListDetail(playlistDTO);
                                }
                            }
                        });
            }
        }
    }

    private void requestAudioPermission() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("获取您的音乐资源权限后将显示本机的音乐")
                .setPositiveButton("授予权限", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MusicListActivity.this,
                                new String[]{Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mMediaCallback);
        }
        if (mMediaBrowser != null) {
            mMediaBrowser.disconnect();
            Glide.get(this).clearMemory();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}


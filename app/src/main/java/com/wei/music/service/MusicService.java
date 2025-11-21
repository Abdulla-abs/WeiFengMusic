package com.wei.music.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.media.MediaBrowserServiceCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wei.music.MusicSessionManager;
import com.wei.music.R;
import com.wei.music.repository.MusicListRepository;
import com.wei.music.service.musicaction.MusicActionContract;
import com.wei.music.utils.MMKVUtils;
import com.wei.music.utils.Resource;
import com.wei.music.utils.ToolUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.Disposable;
import jakarta.inject.Inject;

public class MusicService extends MediaBrowserServiceCompat implements ServiceCallback {
    private static final String NoticeName = "WeiFengMusic";
    private static final int NoticeId = 2969;

    public static final String ACTION_INIT_CURRENT_SONG = "INIT_CURRENT_SONG";
    public static final String ACTION_CHANGE_SONG_LIST = "INIT_CURRENT_SONG";

    public static final String ACTION_LIKE_MUSIC = "LIKE_MUSIC";
    public static final String ACTION_PLAY_MODE = "PLAY_MODE";
    public static final String ACTION_MUSIC_ID = "ACTION_MUSIC_ID";


    private int mPlayModel = SEQUENCE;
    private static final int SINGLE = 0;//单曲循环
    private static final int RANDOM = 1;//随机播放
    private static final int SEQUENCE = 2;//顺序播放

    public static final String MSCQIMusicType = "MediaSessionCompatMusicType";

    private final Random mRandom = new Random();

    private int mPosition = 0;//歌曲位置
    private int mDuration = 0;//歌曲长度
    private int mAudioSessionId = 0;//可视化ID
    private Thread mSeekBarThread;//进度条更新线程

    public void onChangePlayModel(int playModel) {
        mPlayModel = playModel;
        MMKVUtils.savePlayModel(playModel);
        Bundle bundle = new Bundle();
        bundle.putInt("model", mPlayModel);
        mMediaSession.setExtras(bundle);
    }

    private class SeekBarThread implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mPlaybackState.setState(PlaybackStateCompat.STATE_PLAYING, mMediaPlayer.getCurrentPosition(), 1);
                    mMediaSession.setPlaybackState(mPlaybackState.build());
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        }
    }

    private WifiManager.WifiLock mWifiLock;
    private AudioManager mAudioManager;
    private AudioAttributes mAudioAttributes;
    private AudioFocusRequest mAudioFocusRequest;

    //媒体会话，受控端
    private static MediaSessionCompat mMediaSession;
    //播放器
    private MediaPlayer mMediaPlayer;
    //播放状态
    private PlaybackStateCompat.Builder mPlaybackState;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotification;
    private MediaMetadataCompat mMediaMetadata;

    private PendingIntent playAction;
    private PendingIntent prevAction;
    private PendingIntent nextAction;
    private MediaServiceReceiver mMediaServiceRec;

    //    private List<MediaSessionCompat.QueueItem> mLastMusicList = new ArrayList<>();
//
//    private List<MediaSessionCompat.QueueItem> mMusicList = new ArrayList<>();
    private final List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
    private final List<MediaBrowserCompat.MediaItem> mLikeList = new ArrayList<>();

    @Inject
    MusicSessionManager musicSessionManager;
    @Inject
    MusicListRepository musicListRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);

        initMediaPlayer();
        initMediaSession();
        initObserver();
    }

    private void initObserver() {
        musicSessionManager.intent.observeForever(new Observer<MusicActionContract>() {
            @Override
            public void onChanged(MusicActionContract musicActionContract) {

                if (musicActionContract instanceof MusicActionContract.ChangePlayQueue) {
                    MusicActionContract.ChangePlayQueue changePlayQueue = (MusicActionContract.ChangePlayQueue) musicActionContract;
                    queues = changePlayQueue.getReplace();
                    mPosition = changePlayQueue.getStartIndex();
                    mMediaSession.setQueue(queues);
                    startPlay();
                } else if (musicActionContract instanceof MusicActionContract.OnPlayListClick) {
                    MusicActionContract.OnPlayListClick playListClick = (MusicActionContract.OnPlayListClick) musicActionContract;
                    queues = playListClick.getReplace();
                    mPosition = playListClick.getStartIndex();
                    mMediaSession.setQueue(queues);
                    startPlay();
                }


            }
        });
    }

    private List<MediaSessionCompat.QueueItem> queues = Collections.emptyList();

    private void initMediaSession() {
        mMediaSession = new MediaSessionCompat(this, NoticeName);
        mPlaybackState = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mMediaSession.setPlaybackState(mPlaybackState.build());
        mMediaSession.setCallback(mCallback);
        setSessionToken(mMediaSession.getSessionToken());
        mMediaSession.setActive(true);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        Intent intentNext = new Intent("nextMusic");
        nextAction = PendingIntent.getBroadcast(getApplicationContext(), 3, intentNext, flags);

        Intent intentPlay = new Intent("playMusic");
        playAction = PendingIntent.getBroadcast(getApplicationContext(), 2, intentPlay, flags);

        Intent intentPrev = new Intent("prevMusic");
        prevAction = PendingIntent.getBroadcast(getApplicationContext(), 1, intentPrev, flags);

    }

    private void initMediaPlayer() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        intentFilter.addAction("prevMusic");
        intentFilter.addAction("playMusic");
        intentFilter.addAction("nextMusic");
        mMediaServiceRec = new MediaServiceReceiver(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mMediaServiceRec, intentFilter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(mMediaServiceRec, intentFilter);
        }

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mWifiLock = ((WifiManager) getSystemService(WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "musicWifiLock");
        mWifiLock.acquire();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioSessionId = mMediaPlayer.getAudioSessionId();
        ToolUtil.write("AudioId", mAudioSessionId);
        mSeekBarThread = new Thread(new SeekBarThread());

        mPlayModel = ToolUtil.readInt("PlayModel");
    }

    private boolean requestFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mAudioFocusRequest == null) {
                if (mAudioAttributes == null) {
                    mAudioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
                }
                mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(mAudioAttributes).setWillPauseWhenDucked(true).setOnAudioFocusChangeListener(mOnAudioFocusChangeListener).build();
            }
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.requestAudioFocus(mAudioFocusRequest);
        } else {
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    public void startPlay() {
        try {

            if (mMediaPlayer == null) {
                initMediaPlayer();
            }

            mMediaPlayer.reset();

            Bundle extras = queues.get(mPosition).getDescription().getExtras();
            if (extras == null) {
                return;
            }
            int musicType = extras.getInt(MSCQIMusicType, 0);

            String mediaId = queues.get(mPosition).getDescription().getMediaId();
            Disposable subscribe = musicListRepository.fetchSongUrl(musicType, mediaId)
                    .flatMapObservable(new io.reactivex.rxjava3.functions.Function<Resource<Uri>, ObservableSource<Integer>>() {
                        @Override
                        public ObservableSource<Integer> apply(Resource<Uri> uriResource) throws Throwable {
                            return Observable.create(new ObservableOnSubscribe<Integer>() {
                                @Override
                                public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                                    if (uriResource.isSuccess()) {
                                        mMediaPlayer.setDataSource(MusicService.this, uriResource.getData());
                                        mMediaPlayer.prepareAsync();
                                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                            @Override
                                            public void onPrepared(MediaPlayer mp) {
                                                mDuration = mp.getDuration();
                                                emitter.onNext(1);
                                            }
                                        });
                                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                mMediaSession.getController().getTransportControls().skipToNext();
                                                updateNotification();
                                            }
                                        });
                                    } else {
                                        emitter.onError(new RuntimeException(uriResource.getMessage()));
                                    }
                                }
                            });
                        }
                    }).subscribe(new io.reactivex.rxjava3.functions.Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Throwable {
                            if (integer == 1) {
                                mMediaSession.getController().getTransportControls().play();
//                                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
//                                    mMediaPlayer.seekTo(ToolUtil.readInt("MusicProgress"), MediaPlayer.SEEK_CLOSEST);
//                                else mMediaPlayer.seekTo((int) ToolUtil.readInt("MusicProgress"));
                            }
                        }
                    }, new io.reactivex.rxjava3.functions.Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Throwable {

                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mMediaSession.getController().getTransportControls().pause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    mMediaSession.getController().getTransportControls().play();
                    break;
            }
        }
    };

    private final MediaSessionCompat.Callback mCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onPlay() {
            super.onPlay();
            if (mMediaPlayer != null && queues != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlaybackState.setState(PlaybackStateCompat.STATE_PAUSED, mMediaPlayer.getCurrentPosition(), 1);
                    mMediaSession.setPlaybackState(mPlaybackState.build());
                } else {
                    if (requestFocus()) {
                        mMediaPlayer.start();
                        if (!mSeekBarThread.isAlive()) {
                            mSeekBarThread.start();
                            mPlaybackState.setState(PlaybackStateCompat.STATE_PLAYING, mMediaPlayer.getCurrentPosition(), 1);
                            mMediaSession.setPlaybackState(mPlaybackState.build());
                        }
                    }
                }
                updateNotification();
            } else {
                Toast.makeText(getApplication(), "没有数据", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mMediaPlayer != null && queues != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlaybackState.setState(PlaybackStateCompat.STATE_PAUSED, mMediaPlayer.getCurrentPosition(), 1);
                    mMediaSession.setPlaybackState(mPlaybackState.build());
                }
                updateNotification();
            }
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                mMediaPlayer.seekTo(pos, MediaPlayer.SEEK_CLOSEST);
            else mMediaPlayer.seekTo((int) pos);
            mPlaybackState.setState(PlaybackStateCompat.STATE_PLAYING, pos, 1);
            mMediaSession.setPlaybackState(mPlaybackState.build());
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            if (mMediaPlayer != null && queues != null) {
                if (mPlayModel == SEQUENCE) {
                    if (mPosition == queues.size() - 1) {
                        mPosition = 0;
                    } else {
                        mPosition++;
                    }
                } else if (mPlayModel == RANDOM) {
                    mPosition = mRandom.nextInt(queues.size());
                }
                mPlaybackState.setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, 0, 1);
                mMediaSession.setPlaybackState(mPlaybackState.build());

                startPlay();
            }
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            if (mMediaPlayer != null && queues != null) {
                if (mPlayModel == SEQUENCE) {
                    if (mPosition == 0) {
                        mPosition = queues.size() - 1;
                    } else {
                        mPosition--;
                    }
                } else if (mPlayModel == RANDOM) {
                    mPosition = mRandom.nextInt(queues.size());
                }
                mPlaybackState.setState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, 0, 1);
                mMediaSession.setPlaybackState(mPlaybackState.build());

                startPlay();
            }
        }

        @Override
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
            mPosition = (int) id;

            startPlay();
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);

        }
    };

    public static final String ACTION_SONG_LIST = "SONG_LIST_DATA";
    public static final String ACTION_LIKE_SONG = "LIKE_SONG";

    private void getLikeList() {
//        mOkHttpUtil.get(App.getContext(), CloudMusicApi.USER_LISK_LIST + ToolUtil.readString("UserId") + "&timestamp=" + System.currentTimeMillis(), ToolUtil.readString("UserCookie"), mOkHttpUtil.SECOND / 60, new Callback() {
//            @Override
//            public void onFailure(Call p1, IOException p2) {
//            }
//
//            @Override
//            public void onResponse(Call p1, final Response response) throws IOException {
//                UserLikeListBean bean = new UserLikeListBean();
//                bean = mGson.fromJson(response.body().string(), UserLikeListBean.class);
//                if (mLikeList != null)
//                    mLikeList.clear();
//                for (int i = 0; i < bean.ids.size(); i++) {
//                    MediaMetadataCompat mMediaMetadata = new MediaMetadataCompat.Builder()
//                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, bean.ids.get(i))
//                            .build();
//                    mLikeList.add(new MediaBrowserCompat.MediaItem(mMediaMetadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
//                }
//            }
//        });
    }

    private void setMusicMetadata() {
        String name = queues.get(mPosition).getDescription().getTitle().toString();
        String singer = queues.get(mPosition).getDescription().getSubtitle().toString();
        String icon = queues.get(mPosition).getDescription().getDescription().toString();
        String id = queues.get(mPosition).getDescription().getMediaId();
        ToolUtil.write("MusicName", name);
        ToolUtil.write("MusicSinger", singer);
        ToolUtil.write("MusicIcon", icon);
        ToolUtil.write("MusicId", id);
        ToolUtil.write("MusicDuration", mDuration);
        mMediaMetadata = new MediaMetadataCompat.Builder().putString(MediaMetadataCompat.METADATA_KEY_TITLE, name)//歌名
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, singer)//作者
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, icon)//歌曲封面
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)//歌曲id
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mDuration)//歌曲时长
                .build();
        mMediaSession.setMetadata(mMediaMetadata);
    }

    private void updateNotification() {
        setMusicMetadata();
        Glide.with(getApplicationContext()).load(mMediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM)).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(NoticeName, NoticeName, NotificationManager.IMPORTANCE_LOW);
                    mNotificationManager.createNotificationChannel(notificationChannel);
                }
                mNotification = MediaStyleHelper.from(getApplicationContext(), mMediaSession, NoticeName)
                        .setLargeIcon(resource).addAction(R.drawable.ic_previous, "prev", prevAction)
                        .addAction((mMediaPlayer.isPlaying() ? R.drawable.ic_play : R.drawable.ic_pause), "play", playAction)
                        .addAction(R.drawable.ic_next, "next", nextAction);
                mNotificationManager.notify(NoticeId, mNotification.build());
            }
        });
    }

    @Override
    public BrowserRoot onGetRoot(@androidx.annotation.NonNull String clientPackageName, int clientUid, Bundle rootHints) {
        return new BrowserRoot(NoticeName, null);
    }

    @Override
    public void onLoadChildren(@androidx.annotation.NonNull String parentId, MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.detach();
        mediaItems.clear();
        if (parentId.equals("MusicList") && queues != null) {
            for (int i = 0; i < queues.size(); i++) {
                MediaDescriptionCompat description = queues.get(i).getDescription();
                if (description == null){
                    continue;
                }
                MediaMetadataCompat mMediaMetadata = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, description.getTitle().toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, description.getSubtitle().toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, description.getDescription().toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, description.getMediaId())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 100)
                        .build();
                mediaItems.add(new MediaBrowserCompat.MediaItem(mMediaMetadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            }
            result.sendResult(mediaItems);
        } else if (parentId.equals("LikeList")) {
            result.sendResult(mLikeList);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mSeekBarThread.interrupt();
            ToolUtil.write("MusicProgress", mMediaPlayer.getCurrentPosition());
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mWifiLock.release();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);
            else mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
        stopForeground(true);
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NoticeId);
        }
        unregisterReceiver(mMediaServiceRec);
    }

    @Override
    public void onAudioBecomingNoisy() {
        mMediaSession.getController().getTransportControls().pause();
    }

    @Override
    public void onActionPreMusic() {
        mMediaSession.getController().getTransportControls().skipToPrevious();
    }

    @Override
    public void onActionPlayMusic() {
        mMediaSession.getController().getTransportControls().play();
    }

    @Override
    public void onActionNextMusic() {
        mMediaSession.getController().getTransportControls().skipToNext();
    }
}

package com.wei.music.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.media.MediaBrowserServiceCompat;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wei.music.MusicSessionManager;
import com.wei.music.R;
import com.wei.music.repository.MusicRepository;
import com.wei.music.service.musicaction.MusicActionContract;
import com.wei.music.service.playmode.RepeatMode;
import com.wei.music.service.playmode.ShuffleMode;
import com.wei.music.utils.MMKVUtils;
import com.wei.music.utils.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import abbas.fun.myutil.If;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.Disposable;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class MusicService extends MediaBrowserServiceCompat implements ServiceCallback {
    private static final String NoticeName = "WeiFengMusic";
    private static final int NoticeId = 2969;
    public static final String ACTION_INIT_CURRENT_SONG = "INIT_CURRENT_SONG";
    public static final String ACTION_CHANGE_SONG_LIST = "INIT_CURRENT_SONG";
    public static final String ACTION_LIKE_MUSIC = "LIKE_MUSIC";
    public static final String ACTION_PLAY_MODE_FLAG = "PLAY_MODE_FLAG";
    public static final String ACTION_PLAY_MODE = "PLAY_MODE";

    public static final String ACTION_MUSIC_ID = "ACTION_MUSIC_ID";
    public static final String ACTION_SONG_LIST = "SONG_LIST_DATA";
    public static final String MSCQIMusicType = "MediaSessionCompatMusicType";

    private int currentIndex = 0;//歌曲位置
    private int mDuration = 0;//歌曲长度
    private WifiManager.WifiLock mWifiLock;
    private AudioManager mAudioManager;
    private AudioAttributes mAudioAttributes;
    private AudioFocusRequest mAudioFocusRequest;

    //媒体会话，受控端
    private static MediaSessionCompat mediaSession;
    //播放器
    private MediaPlayer player;
    //播放状态
    private PlaybackStateCompat.Builder mPlaybackState;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotification;

    private PendingIntent playAction;
    private PendingIntent prevAction;
    private PendingIntent nextAction;
    private MediaServiceReceiver mMediaServiceRec;

    private final List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
    private final List<MediaBrowserCompat.MediaItem> mLikeList = new ArrayList<>();

    private List<MediaSessionCompat.QueueItem> originQueues = new ArrayList<>();
    private final List<MediaSessionCompat.QueueItem> currentQueue = new ArrayList<>();

    private RepeatMode repeatMode = RepeatMode.OFF;
    private ShuffleMode shuffleMode = ShuffleMode.OFF;

    public static int FIXED_AUDIO_SESSION_ID = -1;

    private PlayerPollingHandler pollingHandler;

    @Inject
    MusicSessionManager musicSessionManager;
    @Inject
    MusicRepository musicRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        initMediaPlayer();
        initMediaSession();
        initObserver();
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

        player = new MediaPlayer();
        player.setAudioSessionId(FIXED_AUDIO_SESSION_ID);
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mDuration = mp.getDuration();
                snapshot.setDuration(mDuration);
                mediaSession.getController().getTransportControls().play();
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaSession.getController().getTransportControls().skipToNext();
                updateNotification();
            }
        });
        mWifiLock = ((WifiManager) getSystemService(WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "musicWifiLock");
        mWifiLock.acquire();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        FIXED_AUDIO_SESSION_ID = player.getAudioSessionId();


        //   mPlayModel = ToolUtil.readInt("PlayModel");
    }

    private void initMediaSession() {
        mediaSession = new MediaSessionCompat(this, NoticeName);
        mPlaybackState = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_SEEK_TO);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMediaButtonReceiver(null);
        mediaSession.setPlaybackState(mPlaybackState.build());
        mediaSession.setCallback(mCallback);
        setSessionToken(mediaSession.getSessionToken());
        mediaSession.setActive(true);

        pollingHandler = new PlayerPollingHandler(Looper.getMainLooper(), player, mPlaybackState, mediaSession);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        Intent intentNext = new Intent("nextMusic");
        nextAction = PendingIntent.getBroadcast(getApplicationContext(), 3, intentNext, flags);

        Intent intentPlay = new Intent("playMusic");
        playAction = PendingIntent.getBroadcast(getApplicationContext(), 2, intentPlay, flags);

        Intent intentPrev = new Intent("prevMusic");
        prevAction = PendingIntent.getBroadcast(getApplicationContext(), 1, intentPrev, flags);

        MusicServiceModeHelper.initMode(mediaSession.getController().getTransportControls());
    }

    private void initObserver() {
        musicSessionManager.action.observeForever(new Observer<MusicActionContract>() {
            @Override
            public void onChanged(MusicActionContract musicActionContract) {
                if (musicActionContract instanceof MusicActionContract.ChangePlayQueue) {
                    MusicActionContract.ChangePlayQueue changePlayQueue = (MusicActionContract.ChangePlayQueue) musicActionContract;
                    queueDatasetChangePlay(changePlayQueue.getReplace(), changePlayQueue.getStartIndex());
                } else if (musicActionContract instanceof MusicActionContract.OnSkipToPosition) {
                    currentIndex = ((MusicActionContract.OnSkipToPosition) musicActionContract).getNewPosition();
                    startPlay();
                } else if (musicActionContract instanceof MusicActionContract.Insert) {
                    MusicActionContract.Insert insertAction = (MusicActionContract.Insert) musicActionContract;
                    if (originQueues.isEmpty() || currentIndex == 0 || currentIndex == originQueues.size() - 1) {
                        originQueues.add(insertAction.insert);
                        currentQueue.add(insertAction.insert);
                    } else {
                        originQueues.add(currentIndex + 1, insertAction.insert);
                        currentQueue.add(currentIndex + 1, insertAction.insert);
                        ++currentIndex;
                    }
                    startPlay();
                }
            }
        });

    }

    private void queueDatasetChangePlay(List<MediaSessionCompat.QueueItem> originQueues, int startIndex) {
        MusicService.this.originQueues = originQueues;
        currentQueue.clear();

        MediaSessionCompat.QueueItem startItem = originQueues.get(startIndex);

        if (shuffleMode == ShuffleMode.ON) {
            currentQueue.addAll(originQueues);
            Collections.shuffle(currentQueue);

            currentIndex = currentQueue.indexOf(startItem);
        } else {
            currentQueue.addAll(originQueues);
        }

        startPlay();
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

            if (player == null) {
                initMediaPlayer();
            }

            player.reset();

            Bundle extras = currentQueue.get(currentIndex).getDescription().getExtras();
            if (extras == null) {
                return;
            }
            int musicType = extras.getInt(MSCQIMusicType, -1);
            snapshot.setMusicType(musicType);

            String mediaId = currentQueue.get(currentIndex).getDescription().getMediaId();
            Disposable subscribe = musicRepository.fetchSongUrl(musicType, mediaId)
                    .subscribe(new io.reactivex.rxjava3.functions.Consumer<Resource<Uri>>() {
                        @Override
                        public void accept(Resource<Uri> uriResource) throws Throwable {
                            if (uriResource.isSuccess()) {
                                snapshot.setUrl(uriResource.getData().toString());
                                player.setDataSource(MusicService.this, uriResource.getData());
                                player.prepareAsync();
//                                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
//                                    mMediaPlayer.seekTo(ToolUtil.readInt("MusicProgress"), MediaPlayer.SEEK_CLOSEST);
//                                else mMediaPlayer.seekTo((int) ToolUtil.readInt("MusicProgress"));
                            } else {
                                //
                                Toast.makeText(MusicService.this, uriResource.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final CurrentMusicSnapshot snapshot = CurrentMusicSnapshot.getInstance();

    private void storeMusicState() {
        try {
            MediaSessionCompat.QueueItem queueItem = currentQueue.get(currentIndex);
            MediaDescriptionCompat description = queueItem.getDescription();
            String defStr = "<unknow>";
            String name = Optional.ofNullable(description.getTitle())
                    .map(CharSequence::toString).orElse(defStr);
            String singer = Optional.ofNullable(description.getSubtitle())
                    .map(CharSequence::toString)
                    .orElse(defStr);
            String icon = Optional.ofNullable(description.getDescription())
                    .map(CharSequence::toString)
                    .orElse(defStr);
            String id = Optional.ofNullable(description.getMediaId())
                    .orElse(defStr);
            snapshot.setMusicName(name)
                    .setArtist(singer)
                    .setAlbum(icon)
                    .setMusicId(Long.parseLong(id));
            MMKVUtils.putMusicSnapshot(snapshot);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mediaSession.getController().getTransportControls().pause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    mediaSession.getController().getTransportControls().play();
                    break;
            }
        }
    };

    private final MediaSessionCompat.Callback mCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onSetRepeatMode(int repeatMode) {
            super.onSetRepeatMode(repeatMode);
            switch (repeatMode) {
                case PlaybackStateCompat.REPEAT_MODE_NONE:
                    MusicService.this.repeatMode = RepeatMode.OFF;
                    break;
                case PlaybackStateCompat.REPEAT_MODE_ONE:
                    MusicService.this.repeatMode = RepeatMode.ONE;
                    break;
                case PlaybackStateCompat.REPEAT_MODE_ALL:
                case PlaybackStateCompat.REPEAT_MODE_GROUP:
                    MusicService.this.repeatMode = RepeatMode.ALL;
                    break;
                default:
                    MusicService.this.repeatMode = RepeatMode.OFF;
                    break;
            }

            mediaSession.setRepeatMode(repeatMode);
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            super.onSetShuffleMode(shuffleMode);

            boolean newModeEnabled = (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL);

            if (newModeEnabled && MusicService.this.shuffleMode == ShuffleMode.OFF) {
                // 开启随机：打乱队列，但保持当前歌曲不变
                MediaSessionCompat.QueueItem currentItem = (currentIndex >= 0 && currentIndex < currentQueue.size())
                        ? currentQueue.get(currentIndex) : null;

                currentQueue.clear();
                currentQueue.addAll(originQueues);
                Collections.shuffle(currentQueue);

                if (currentItem != null) {
                    currentIndex = currentQueue.indexOf(currentItem);
                    if (currentIndex == -1) currentIndex = 0;
                }
            } else if (!newModeEnabled && MusicService.this.shuffleMode == ShuffleMode.ON) {
                // 关闭随机：恢复原始顺序，保持当前歌曲位置
                MediaSessionCompat.QueueItem currentItem = (currentIndex >= 0 && currentIndex < currentQueue.size())
                        ? currentQueue.get(currentIndex) : null;

                currentQueue.clear();
                currentQueue.addAll(originQueues);

                if (currentItem != null) {
                    currentIndex = currentQueue.indexOf(currentItem);
                    if (currentIndex == -1) currentIndex = 0;
                }
            }

            MusicService.this.shuffleMode = newModeEnabled ? ShuffleMode.ON : ShuffleMode.OFF;

            mediaSession.setShuffleMode(shuffleMode);
            // 通知所有客户端（通知栏、锁屏、Widget、手表）队列变了
            mediaSession.setQueue(currentQueue);

            updatePlaybackState();
        }

        @Override
        public void onPlay() {
            super.onPlay();
            if (player != null && currentQueue != null) {
                if (player.isPlaying()) {
                    player.pause();
                    Bundle bundle = new Bundle();
                    bundle.putInt(MUSIC_STATE_POSITION, currentIndex);
                    mPlaybackState.setExtras(bundle);
                    mPlaybackState.setState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition(), 1);
                    mediaSession.setPlaybackState(mPlaybackState.build());
                } else {
                    if (requestFocus()) {
                        player.start();
                        pollingHandler.start();
                        Bundle bundle = new Bundle();
                        bundle.putInt(MUSIC_STATE_POSITION, currentIndex);
                        mPlaybackState.setExtras(bundle);
                        mPlaybackState.setState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition(), 1);
                        mediaSession.setPlaybackState(mPlaybackState.build());
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
            if (player != null && currentQueue != null) {
                if (player.isPlaying()) {
                    player.pause();

                    Bundle bundle = new Bundle();
                    bundle.putInt(MUSIC_STATE_POSITION, currentIndex);
                    mPlaybackState.setExtras(bundle);
                    mPlaybackState.setState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition(), 1);
                    mediaSession.setPlaybackState(mPlaybackState.build());
                }
                updateNotification();
            }
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                player.seekTo(pos, MediaPlayer.SEEK_CLOSEST);
            else player.seekTo((int) pos);
            mPlaybackState.setState(PlaybackStateCompat.STATE_PLAYING, pos, 1);
            mediaSession.setPlaybackState(mPlaybackState.build());
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            if (repeatMode == RepeatMode.ONE) {
                // 单曲循环：不切歌，只从头开始
                mediaSession.getController().getTransportControls().seekTo(0);
                return;
            }

            if (currentQueue.isEmpty()) return;

            if (repeatMode == RepeatMode.ALL) {
                // 列表循环
                currentIndex = (currentIndex + 1) % currentQueue.size();
            } else {
                // 顺序播放（OFF）
                if (currentIndex < currentQueue.size() - 1) {
                    currentIndex++;
                } else {
                    // 已经最后一首，停在末尾（标准行为）
                    updatePlaybackState(player.getDuration());
                    return;
                }
            }

            mPlaybackState.setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, 0, 1);
            mediaSession.setPlaybackState(mPlaybackState.build());
            startPlay();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            if (currentQueue.isEmpty()) return;

            if (currentIndex > 0) {
                currentIndex--;
            } else {
                currentIndex = currentQueue.size() - 1; // 循环到最后一首
            }

            mPlaybackState.setState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, 0, 1);
            mediaSession.setPlaybackState(mPlaybackState.build());

            startPlay();
        }

        @Override
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
            currentIndex = (int) id;

            startPlay();
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
            If.of(action)
                    .is(ACTION_PLAY_MODE_FLAG, new Consumer<String>() {
                        @Override
                        public void accept(String unused) {

                        }
                    })
                    .end();
        }
    };

    private void cycleRepeatMode() {
        switch (repeatMode) {
            case OFF:
                repeatMode = RepeatMode.ALL;
                break;
            case ALL:
                repeatMode = RepeatMode.ONE;
                break;
            case ONE:
                repeatMode = RepeatMode.OFF;
                break;
        }
    }

    private void toggleShuffleMode() {
        shuffleMode = (shuffleMode == ShuffleMode.OFF) ? ShuffleMode.ON : ShuffleMode.OFF;
        // 这里加你的随机逻辑（打乱队列等）
    }

    private void updatePlaybackState() {
        long position = player != null ? player.getCurrentPosition() : 0L;
        updatePlaybackState(position);
    }

    private void updatePlaybackState(long position) {
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .setState(player.isPlaying() ? PlaybackStateCompat.STATE_PLAYING
                                : PlaybackStateCompat.STATE_PAUSED,
                        position, 1.0f);

        if (currentIndex >= 0 && currentIndex < currentQueue.size()) {
            builder.setActiveQueueItemId(currentQueue.get(currentIndex).getQueueId());
        }

        mediaSession.setPlaybackState(builder.build());
        //mediaSession.setQueue(currentQueue);
    }

    private Optional<MediaMetadataCompat> resetMusicMetadata() {
        try {
            if (currentQueue.isEmpty()) return Optional.empty();
            MediaDescriptionCompat description = currentQueue.get(currentIndex).getDescription();
            MediaMetadataCompat mediaMetadataCompat = createMediaMetadataCompat(description);
            mediaSession.setMetadata(mediaMetadataCompat);
            return Optional.of(mediaMetadataCompat);
        } catch (IndexOutOfBoundsException e) {
            LogUtils.e(e);
        }
        return Optional.empty();
    }

    private void updateNotification() {
        Optional<MediaMetadataCompat> mediaMetadataCompatOpt = resetMusicMetadata();
        if (mediaMetadataCompatOpt.isEmpty()) {
            return;
        }
        MediaMetadataCompat mediaMetadataCompat = mediaMetadataCompatOpt.get();
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(mediaMetadataCompat.getString(MediaMetadata.METADATA_KEY_ALBUM))
                .into(new CustomTarget<Bitmap>() {  // ← 改成 CustomTarget

                    @Override
                    public void onResourceReady(@androidx.annotation.NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        // 原来的 onResourceReady 内容完全不动
                        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel = new NotificationChannel(
                                    NoticeName, NoticeName, NotificationManager.IMPORTANCE_LOW);
                            mNotificationManager.createNotificationChannel(notificationChannel);
                        }

                        mNotification = MediaStyleHelper.from(getApplicationContext(), mediaSession, NoticeName)
                                .setLargeIcon(resource)  // ← 这里直接用拿到的 Bitmap
                                .addAction(R.drawable.ic_previous, "prev", prevAction)
                                .addAction(player.isPlaying() ? R.drawable.ic_play : R.drawable.ic_pause, "play", playAction)
                                .addAction(R.drawable.ic_next, "next", nextAction);

                        mNotificationManager.notify(NoticeId, mNotification.build());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // 可选：加载被清除时（比如 Service 被销毁）执行清理
                        // 如果你担心内存泄漏，这里可以把 resource = null
                    }
                });
    }

    @Override
    public BrowserRoot onGetRoot(@androidx.annotation.NonNull String clientPackageName, int clientUid, Bundle rootHints) {
        return new BrowserRoot(NoticeName, null);
    }

    public static final String MUSIC_ALL = "WF_MUSIC_ALL";
    public static final String MUSIC_FAVORITE = "WF_MUSIC_FAVORITE";
    public static final String MUSIC_STATE_POSITION = "MUSIC_STATE_POSITION";

    @Override
    public void onLoadChildren(@androidx.annotation.NonNull String parentId, MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.detach();
        mediaItems.clear();
        if (parentId.equals(MUSIC_ALL) && currentQueue != null) {
            for (int i = 0; i < currentQueue.size(); i++) {
                MediaDescriptionCompat description = currentQueue.get(i).getDescription();
                if (description == null) {
                    continue;
                }
                MediaMetadataCompat mediaMetadataCompat = createMediaMetadataCompat(description);
                mediaItems.add(
                        new MediaBrowserCompat.MediaItem(
                                mediaMetadataCompat.getDescription(),
                                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
                );
                Bundle bundle = new Bundle();
                bundle.putInt(MUSIC_STATE_POSITION, currentIndex);
                PlaybackStateCompat stateCompat = new PlaybackStateCompat.Builder()
                        .setExtras(bundle)
                        .build();
                mediaSession.setPlaybackState(stateCompat);
            }
            result.sendResult(mediaItems);
        } else if (parentId.equals(MUSIC_FAVORITE)) {
            result.sendResult(mLikeList);
        }
    }

    private MediaMetadataCompat createMediaMetadataCompat(MediaDescriptionCompat mediaDescriptionCompat) {
        MediaMetadataCompat.Builder mediaMetadataCompatBuilder = new MediaMetadataCompat.Builder();
        if (mediaDescriptionCompat == null) return mediaMetadataCompatBuilder.build();
        String defStr = "<unknow>";
        String name = Optional.ofNullable(mediaDescriptionCompat.getTitle())
                .map(CharSequence::toString).orElse(defStr);
        String singer = Optional.ofNullable(mediaDescriptionCompat.getSubtitle())
                .map(CharSequence::toString)
                .orElse(defStr);
        String icon = Optional.ofNullable(mediaDescriptionCompat.getDescription())
                .map(CharSequence::toString)
                .orElse(defStr);
        String id = Optional.ofNullable(mediaDescriptionCompat.getMediaId())
                .orElse(defStr);
        return mediaMetadataCompatBuilder
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, name)//歌名
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, singer)//作者
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, icon)//歌曲封面
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)//歌曲id
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mDuration)//歌曲时长
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            pollingHandler.stop();
            player.stop();
            player.release();
            player = null;
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
        mediaSession.getController().getTransportControls().pause();
    }

    @Override
    public void onActionPreMusic() {
        mediaSession.getController().getTransportControls().skipToPrevious();
    }

    @Override
    public void onActionPlayMusic() {
        mediaSession.getController().getTransportControls().play();
    }

    @Override
    public void onActionNextMusic() {
        mediaSession.getController().getTransportControls().skipToNext();
    }
}

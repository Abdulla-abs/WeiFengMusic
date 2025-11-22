package com.wei.music.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.wei.music.R;
import com.wei.music.activity.play.PlayerActivity;

public class MediaStyleHelper {

    /**
     * 完全修复 Android 12+ PendingIntent 必须指定 FLAG_IMMUTABLE 的问题
     */
    public static NotificationCompat.Builder from(Context context, MediaSessionCompat mediaSession, String tag) {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        // ---------- 关键修复：点击通知进入 PlayerActivity 的 PendingIntent ----------
        Intent intent = new Intent(context, PlayerActivity.class);
        // 推荐加上这个 flag，避免多次点击创建多个任务栈
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        // 如果你的 minSdk < 23，下面的写法也能兼容（系统会自动忽略 IMMUTABLE）
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlags);

        // ---------- 停止按钮的 PendingIntent（系统自带的 MediaButtonReceiver）也需要加 flag ----------
        // Android 12+ 这里也会崩溃，所以同样要加上 FLAG_IMMUTABLE
        PendingIntent deleteIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(
                context,
                PlaybackStateCompat.ACTION_STOP
        );
        // 如果你担心某些旧版 support-library 没有加 flag，可以自行再包一层（下面是保险写法）：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // Android 12
            PendingIntent safeDeleteIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    new Intent(context, MediaButtonReceiver.class)
                            .setAction(Intent.ACTION_MEDIA_BUTTON)
                            .putExtra(Intent.EXTRA_KEY_EVENT, PlaybackStateCompat.ACTION_STOP),
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            deleteIntent = safeDeleteIntent;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, tag);
        builder
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getSubtitle())  // 可选，有些人喜欢这里显示歌手
                .setSmallIcon(R.drawable.ic_music)
                .setLargeIcon(description.getIconBitmap())  // 如果有封面的话可以直接用
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))  // 折叠时显示前三个按钮
                .setDeleteIntent(deleteIntent)   // 滑动删除/点击关闭按钮时停止播放
                .setContentIntent(pendingIntent) // 点击通知打开 PlayerActivity
                .setOngoing(true)                // 常驻通知，不可手动清除
                .setAutoCancel(false);

        return builder;
    }
}
// MusicServiceModeHelper.java
package com.wei.music.service;

import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.wei.music.utils.MMKVUtils;

public class MusicServiceModeHelper {

    // 三种对外模式（你 UI 上只显示这三个按钮）
    public enum PlayMode {
        SINGLE_CIRCLE(1),   // 单曲循环
        LIST_CIRCLE(2),     // 列表循环
        SHUFFLE_CIRCLE(3);   // 随机播放（最常用）

        public final int flag;

        PlayMode(int flag) {
            this.flag = flag;
        }

        public static PlayMode valueOfFlag(int flag) {
            for (PlayMode value : PlayMode.values()) {
                if (value.flag == flag) {
                    return value;
                }
            }
            return PlayMode.LIST_CIRCLE;
        }
    }

    // 当前模式（可保存到 MMKV）
    private static PlayMode currentMode = PlayMode.LIST_CIRCLE;

    public static void initMode(MediaControllerCompat.TransportControls controls) {
        currentMode = MMKVUtils.getCurrentPlayMode();
        setUpMode(controls);
    }


    public static void toggleMode(MediaControllerCompat.TransportControls controls) {
        if (currentMode == PlayMode.SINGLE_CIRCLE) {
            setListCircle(controls);
        } else if (currentMode == PlayMode.LIST_CIRCLE) {
            setShuffleCircle(controls);
        } else if (currentMode == PlayMode.SHUFFLE_CIRCLE) {
            setSingleCircle(controls);
        }
        MMKVUtils.putPlayMode(currentMode);
    }

    // ===== 对外 API：只暴露这三个方法 =====
    public static void setSingleCircle(MediaControllerCompat.TransportControls controls) {
        currentMode = PlayMode.SINGLE_CIRCLE;
        setUpMode(controls);
    }

    public static void setListCircle(MediaControllerCompat.TransportControls controls) {
        currentMode = PlayMode.LIST_CIRCLE;
        setUpMode(controls);
    }

    public static void setShuffleCircle(MediaControllerCompat.TransportControls controls) {
        currentMode = PlayMode.SHUFFLE_CIRCLE;
        setUpMode(controls);
    }

    private static void setUpMode(MediaControllerCompat.TransportControls controls){
        switch (currentMode){
            case LIST_CIRCLE:
                controls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                controls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                break;
            case SINGLE_CIRCLE:
                controls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                controls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                break;
            case SHUFFLE_CIRCLE:
                controls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                controls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
                break;
        }
    }

    // 可选：获取当前模式（UI 高亮用）
    public static PlayMode getCurrentMode() {
        return currentMode;
    }
}
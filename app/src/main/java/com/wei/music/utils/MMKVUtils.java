package com.wei.music.utils;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.GsonUtils;
import com.tencent.mmkv.MMKV;
import com.wei.music.bean.SongListBean;
import com.wei.music.bean.UserLoginBean;

import java.util.Optional;
import java.util.Set;

/**
 * MMKV工具类
 * 基于腾讯MMKV的键值对存储工具
 */
public class MMKVUtils {

    private static final MMKV mmkv;

    static {
        mmkv = MMKV.defaultMMKV();
    }

    // 私有构造函数
    private MMKVUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    // ==================== String 相关操作 ====================

    /**
     * 存储String值
     */
    public static void putString(String key, String value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取String值
     */
    public static String getString(String key) {
        return mmkv.decodeString(key, "");
    }

    /**
     * 获取String值（带默认值）
     */
    public static String getString(String key, String defaultValue) {
        return mmkv.decodeString(key, defaultValue);
    }

    // ==================== Int 相关操作 ====================

    /**
     * 存储Int值
     */
    public static void putInt(String key, int value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取Int值
     */
    public static int getInt(String key) {
        return mmkv.decodeInt(key, 0);
    }

    /**
     * 获取Int值（带默认值）
     */
    public static int getInt(String key, int defaultValue) {
        return mmkv.decodeInt(key, defaultValue);
    }

    // ==================== Long 相关操作 ====================

    /**
     * 存储Long值
     */
    public static void putLong(String key, long value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取Long值
     */
    public static long getLong(String key) {
        return mmkv.decodeLong(key, 0L);
    }

    /**
     * 获取Long值（带默认值）
     */
    public static long getLong(String key, long defaultValue) {
        return mmkv.decodeLong(key, defaultValue);
    }

    // ==================== Boolean 相关操作 ====================

    /**
     * 存储Boolean值
     */
    public static void putBoolean(String key, boolean value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取Boolean值
     */
    public static boolean getBoolean(String key) {
        return mmkv.decodeBool(key, false);
    }

    /**
     * 获取Boolean值（带默认值）
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return mmkv.decodeBool(key, defaultValue);
    }

    // ==================== Float 相关操作 ====================

    /**
     * 存储Float值
     */
    public static void putFloat(String key, float value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取Float值
     */
    public static float getFloat(String key) {
        return mmkv.decodeFloat(key, 0f);
    }

    /**
     * 获取Float值（带默认值）
     */
    public static float getFloat(String key, float defaultValue) {
        return mmkv.decodeFloat(key, defaultValue);
    }

    // ==================== Double 相关操作 ====================

    /**
     * 存储Double值
     */
    public static void putDouble(String key, double value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取Double值
     */
    public static double getDouble(String key) {
        return mmkv.decodeDouble(key, 0.0);
    }

    /**
     * 获取Double值（带默认值）
     */
    public static double getDouble(String key, double defaultValue) {
        return mmkv.decodeDouble(key, defaultValue);
    }

    // ==================== ByteArray 相关操作 ====================

    /**
     * 存储ByteArray
     */
    public static void putByteArray(String key, byte[] value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取ByteArray
     */
    public static byte[] getByteArray(String key) {
        return mmkv.decodeBytes(key);
    }

    /**
     * 获取ByteArray（带默认值）
     */
    public static byte[] getByteArray(String key, byte[] defaultValue) {
        return mmkv.decodeBytes(key, defaultValue);
    }

    // ==================== 集合操作 ====================

    /**
     * 存储String集合
     */
    public static void putStringSet(String key, Set<String> value) {
        mmkv.encode(key, value);
    }

    /**
     * 获取String集合
     */
    public static Set<String> getStringSet(String key) {
        return mmkv.decodeStringSet(key, null);
    }

    /**
     * 获取String集合（带默认值）
     */
    public static Set<String> getStringSet(String key, Set<String> defaultValue) {
        return mmkv.decodeStringSet(key, defaultValue);
    }

    // ==================== 其他操作 ====================

    /**
     * 检查是否包含某个key
     */
    public static boolean contains(String key) {
        return mmkv.contains(key);
    }

    /**
     * 删除某个key
     */
    public static void remove(String key) {
        mmkv.removeValueForKey(key);
    }

    /**
     * 批量删除keys
     */
    public static void removeKeys(String[] keys) {
        mmkv.removeValuesForKeys(keys);
    }

    /**
     * 清除所有数据
     */
    public static void clearAll() {
        mmkv.clearAll();
    }

    /**
     * 获取所有key
     */
    public static String[] getAllKeys() {
        return mmkv.allKeys();
    }

    /**
     * 获取总大小
     */
    public static long totalSize() {
        return mmkv.totalSize();
    }

    /**
     * 获取实际大小
     */
    public static long actualSize() {
        return mmkv.actualSize();
    }

    /**
     * 同步操作（确保数据写入磁盘）
     */
    public static void sync() {
        mmkv.sync();
    }

    /**
     * 异步操作
     */
    public static void async() {
        mmkv.async();
    }

    /**
     * 获取MMKV实例
     */
    public static MMKV getMMKV() {
        return mmkv;
    }

    /**
     * 使用不同的MMKV实例
     */
    public static MMKV getMMKV(String mmapID) {
        return MMKV.mmkvWithID(mmapID);
    }

    public static boolean isFirstRun() {
        return mmkv.decodeBool("isFirstFun", true);
    }

    public static void appRun() {
        mmkv.encode("isFirstFun", false);
    }

    public static void putUser(UserLoginBean userLoginBean) {
        String userStr = GsonUtils.toJson(userLoginBean, UserLoginBean.class);
        mmkv.encode("userLoginData", userStr);
    }

    public static void putUser(String userLoginStr) {
        mmkv.encode("userLoginData", userLoginStr);
    }

    public static UserLoginBean getUserLoginBean() {
        String userLoginData = mmkv.decodeString("userLoginData");
        try {
            return GsonUtils.fromJson(userLoginData, UserLoginBean.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final String CURRENT_SONG_LIST = "CurrentSongList";

    public static @Nullable SongListBean currentSongData() {
        Optional<String> currentSongList = Optional.ofNullable(mmkv.decodeString(CURRENT_SONG_LIST));
        return currentSongList.map(s -> GsonUtils.fromJson(s, SongListBean.class)).orElse(null);
    }

    public static @Nullable String currentSongDataStr() {
        return mmkv.decodeString(CURRENT_SONG_LIST);
    }

    public static void saveCurrentSongList(SongListBean data) {
        mmkv.putString(CURRENT_SONG_LIST, GsonUtils.toJson(data));
    }

    public static int lastSongListPos() {
        return mmkv.decodeInt("MusicPosition");
    }

    public static boolean hasCurrentSongList() {
        return currentSongData() != null;
    }

    public static void putUserCookie(String cookie) {
        mmkv.encode("UserCookie", cookie);
    }

    @Nullable
    public static String getUserCookie() {
        return mmkv.decodeString("UserCookie");
    }


    public static final String PLAY_MODEL = "PLAY_MODEL";

    public static void savePlayModel(int playModel) {
        mmkv.putInt(PLAY_MODEL, playModel);
    }
}
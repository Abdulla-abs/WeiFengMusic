package com.wei.music.utils;

import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.DecimalFormat;

public class AudioFileReader {

    public byte[] readAudioFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Files.readAllBytes(file.toPath());
        }
        return new byte[]{};
    }

    public InputStream getAudioFileStream(String filePath) throws FileNotFoundException {
        return new FileInputStream(filePath);
    }

    // 获取音频文件信息
    public void printAudioFileInfo(AudioFileFetcher.AudioFile audioFile) {
        Log.d("AudioInfo", "名称: " + audioFile.getName());
        Log.d("AudioInfo", "路径: " + audioFile.getPath());
        Log.d("AudioInfo", "大小: " + formatFileSize(audioFile.getSize()));
        Log.d("AudioInfo", "时长: " + formatDuration(audioFile.getDuration()));
        Log.d("AudioInfo", "艺术家: " + audioFile.getArtist());
        Log.d("AudioInfo", "专辑: " + audioFile.getAlbum());
    }

    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private String formatDuration(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
package com.wei.music.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioScanner {

    public List<File> findAudioFiles(File directory) {
        List<File> audioFiles = new ArrayList<>();
        scanDirectory(directory, audioFiles);
        return audioFiles;
    }

    private void scanDirectory(File directory, List<File> audioFiles) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // 递归扫描子目录
                scanDirectory(file, audioFiles);
            } else if (isAudioFile(file)) {
                audioFiles.add(file);
            }
        }
    }

    private boolean isAudioFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".mp3") ||
                name.endsWith(".wav") ||
                name.endsWith(".aac") ||
                name.endsWith(".flac") ||
                name.endsWith(".m4a") ||
                name.endsWith(".ogg");
    }
}

// 使用示例
//AudioScanner scanner = new AudioScanner();
//File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
//List<File> audioFiles = scanner.findAudioFiles(musicDir);
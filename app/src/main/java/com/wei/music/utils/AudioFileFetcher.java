package com.wei.music.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.wei.music.bean.SongListBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioFileFetcher {

    public static final int LOCAL_SONG_LIST_ID = -1;
    public final static SongListBean cachedLocalSongs = new SongListBean(
            "本地歌曲歌单",
            0,
            "",
            LOCAL_SONG_LIST_ID
    );
    public static List<AudioFile> cachedLocalSongsList = Collections.emptyList();

    public static List<AudioFile> getAudioFiles(Context context) {
        List<AudioFile> audioFiles = new ArrayList<>();

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATE_ADDED
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder)) {

            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);

                do {
                    AudioFile audioFile = new AudioFile();
                    audioFile.setId(cursor.getLong(idColumn));
                    audioFile.setName(cursor.getString(nameColumn));
                    audioFile.setPath(cursor.getString(pathColumn));
                    audioFile.setSize(cursor.getLong(sizeColumn));
                    audioFile.setDuration(cursor.getLong(durationColumn));
                    audioFile.setArtist(cursor.getString(artistColumn));
                    audioFile.setAlbum(cursor.getString(albumColumn));

                    audioFiles.add(audioFile);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cachedLocalSongs.setSize(audioFiles.size());
        cachedLocalSongsList = audioFiles;

        return audioFiles;
    }

    public static class AudioFile {
        private long id;
        private String name;
        private String path;
        private long size;
        private long duration;
        private String artist;
        private String album;

        // Getter 和 Setter 方法
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }
    }
}
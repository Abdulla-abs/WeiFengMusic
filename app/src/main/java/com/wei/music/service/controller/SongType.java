package com.wei.music.service.controller;

public enum SongType {
    LOCAL(-1), REMOTE(1);

    public final int type;

    SongType(int type) {
        this.type = type;
    }
}

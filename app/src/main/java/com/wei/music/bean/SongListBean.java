package com.wei.music.bean;

import java.util.Objects;

public class SongListBean {
    private String title;
    private int size;
    private String image;
    private int id;

    public SongListBean() {
    }

    public SongListBean(String title, int size, String image, int id) {
        this.title = title;
        this.size = size;
        this.image = image;
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof SongListBean)) return false;

        SongListBean that = (SongListBean) o;
        return size == that.size && id == that.id && Objects.equals(title, that.title) && Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(title);
        result = 31 * result + size;
        result = 31 * result + Objects.hashCode(image);
        result = 31 * result + id;
        return result;
    }
}

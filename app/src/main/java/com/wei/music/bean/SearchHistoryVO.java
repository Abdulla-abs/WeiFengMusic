package com.wei.music.bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class SearchHistoryVO {

    @PrimaryKey
    @NonNull
    private String content;

    private Long searchTime;

    public SearchHistoryVO() {
        content = "unKnow";
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }


    public Long getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(Long searchTime) {
        this.searchTime = searchTime;
    }
}

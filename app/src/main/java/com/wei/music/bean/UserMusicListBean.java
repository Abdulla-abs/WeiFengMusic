package com.wei.music.bean;

import java.util.ArrayList;
import java.util.List;


//歌单数据

public class UserMusicListBean {

    private PlayList playlist;

    public static class PlayList {

        private String coverImgUrl;//歌单封面
        private String name;//歌单名
        private String description;//歌单介绍
        private List<Tracks> tracks = new ArrayList<>();

        public static class Tracks {
            private String name;//歌曲名
            private String id;//歌曲ID

            private List<Ar> ar = new ArrayList<>();

            public static class Ar {
                private String id;//歌手ID
                private String name;//歌手名

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }

            private Al al;

            public static class Al {
                private String picUrl;//歌曲封面

                public String getPicUrl() {
                    return picUrl;
                }

                public void setPicUrl(String picUrl) {
                    this.picUrl = picUrl;
                }
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public List<Ar> getAr() {
                return ar;
            }

            public void setAr(List<Ar> ar) {
                this.ar = ar;
            }

            public Al getAl() {
                return al;
            }

            public void setAl(Al al) {
                this.al = al;
            }
        }

        public String getCoverImgUrl() {
            return coverImgUrl;
        }

        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Tracks> getTracks() {
            return tracks;
        }

        public void setTracks(List<Tracks> tracks) {
            this.tracks = tracks;
        }
    }

    public PlayList getPlaylist() {
        return playlist;
    }

    public void setPlaylist(PlayList playlist) {
        this.playlist = playlist;
    }
}

package com.wei.music.bean;

/**
 * {"sgc":false,"sfy":false,"qfy":false,"needDesc":true,"pureMusic":true,"lrc":{"version":1,"lyric":"[00:05.00]纯音乐，请欣赏\n"},"code":200,"briefDesc":null}
 */
public class LyricDTO {

    /**
     * sgc
     */
    private Boolean sgc;
    /**
     * sfy
     */
    private Boolean sfy;
    /**
     * qfy
     */
    private Boolean qfy;
    /**
     * lrc
     */
    private LrcDTO lrc;
    /**
     * klyric
     */
    private KlyricDTO klyric;
    /**
     * tlyric
     */
    private TlyricDTO tlyric;
    /**
     * romalrc
     */
    private RomalrcDTO romalrc;
    /**
     * code
     */
    private Integer code;

    public Boolean getSgc() {
        return sgc;
    }

    public void setSgc(Boolean sgc) {
        this.sgc = sgc;
    }

    public Boolean getSfy() {
        return sfy;
    }

    public void setSfy(Boolean sfy) {
        this.sfy = sfy;
    }

    public Boolean getQfy() {
        return qfy;
    }

    public void setQfy(Boolean qfy) {
        this.qfy = qfy;
    }

    public LrcDTO getLrc() {
        return lrc;
    }

    public void setLrc(LrcDTO lrc) {
        this.lrc = lrc;
    }

    public KlyricDTO getKlyric() {
        return klyric;
    }

    public void setKlyric(KlyricDTO klyric) {
        this.klyric = klyric;
    }

    public TlyricDTO getTlyric() {
        return tlyric;
    }

    public void setTlyric(TlyricDTO tlyric) {
        this.tlyric = tlyric;
    }

    public RomalrcDTO getRomalrc() {
        return romalrc;
    }

    public void setRomalrc(RomalrcDTO romalrc) {
        this.romalrc = romalrc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static class LrcDTO {
        /**
         * version
         */
        private Integer version;
        /**
         * lyric
         */
        private String lyric;

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public String getLyric() {
            return lyric;
        }

        public void setLyric(String lyric) {
            this.lyric = lyric;
        }
    }

    public static class KlyricDTO {
        /**
         * version
         */
        private Integer version;
        /**
         * lyric
         */
        private String lyric;

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public String getLyric() {
            return lyric;
        }

        public void setLyric(String lyric) {
            this.lyric = lyric;
        }
    }

    public static class TlyricDTO {
        /**
         * version
         */
        private Integer version;
        /**
         * lyric
         */
        private String lyric;

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public String getLyric() {
            return lyric;
        }

        public void setLyric(String lyric) {
            this.lyric = lyric;
        }
    }

    public static class RomalrcDTO {
        /**
         * version
         */
        private Integer version;
        /**
         * lyric
         */
        private String lyric;

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public String getLyric() {
            return lyric;
        }

        public void setLyric(String lyric) {
            this.lyric = lyric;
        }
    }
}

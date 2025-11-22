package com.wei.music.bean;

import java.util.List;

public class MusicUrlDTO {

    /**
     * code : 200
     * data : [{"id":27896028,"url":"http://m8.music.126.net/20251119203007/5ba39b5fd5ba77c243ff50f235c5c38d/ymusic/ece7/0d0f/68d5/5baa1cad07aeb3d93334f6c8151d3b3e.mp3?vuutv=thOY8bROnOqIzYJsMFRVoKPI5881Lh8J62rk5u/xk/isMF5YHYIMCT9hUQJ3Udqq3DYBiZQ2XDQDFh2jP/pNKQNnC826HLod2Iy785VrM+/GwWKsQcTW5bGXE7sixOXZDgePxvBAuY4hVGYoE2XM2l1P/JaCIMeu6ck4S5Y4/z4=","br":128000,"size":1130769,"md5":"5baa1cad07aeb3d93334f6c8151d3b3e","code":200,"expi":1200,"type":"mp3","gain":-7.9477,"peak":1,"closedGain":-6,"closedPeak":1.2207,"fee":0,"uf":null,"payed":0,"flag":1867776,"canExtend":false,"freeTrialInfo":null,"level":"standard","encodeType":"mp3","channelLayout":null,"freeTrialPrivilege":{"resConsumable":false,"userConsumable":false,"listenType":null,"cannotListenReason":null,"playReason":null,"freeLimitTagType":null},"freeTimeTrialPrivilege":{"resConsumable":false,"userConsumable":false,"type":0,"remainTime":0},"urlSource":0,"rightSource":0,"podcastCtrp":null,"effectTypes":null,"time":70000,"message":null,"levelConfuse":null,"musicId":"65375555","accompany":null,"sr":44100,"auEff":null,"immerseType":null}]
     */

    private int code;
    /**
     * id : 27896028
     * url : http://m8.music.126.net/20251119203007/5ba39b5fd5ba77c243ff50f235c5c38d/ymusic/ece7/0d0f/68d5/5baa1cad07aeb3d93334f6c8151d3b3e.mp3?vuutv=thOY8bROnOqIzYJsMFRVoKPI5881Lh8J62rk5u/xk/isMF5YHYIMCT9hUQJ3Udqq3DYBiZQ2XDQDFh2jP/pNKQNnC826HLod2Iy785VrM+/GwWKsQcTW5bGXE7sixOXZDgePxvBAuY4hVGYoE2XM2l1P/JaCIMeu6ck4S5Y4/z4=
     * br : 128000
     * size : 1130769
     * md5 : 5baa1cad07aeb3d93334f6c8151d3b3e
     * code : 200
     * expi : 1200
     * type : mp3
     * gain : -7.9477
     * peak : 1
     * closedGain : -6
     * closedPeak : 1.2207
     * fee : 0
     * uf : null
     * payed : 0
     * flag : 1867776
     * canExtend : false
     * freeTrialInfo : null
     * level : standard
     * encodeType : mp3
     * channelLayout : null
     * freeTrialPrivilege : {"resConsumable":false,"userConsumable":false,"listenType":null,"cannotListenReason":null,"playReason":null,"freeLimitTagType":null}
     * freeTimeTrialPrivilege : {"resConsumable":false,"userConsumable":false,"type":0,"remainTime":0}
     * urlSource : 0
     * rightSource : 0
     * podcastCtrp : null
     * effectTypes : null
     * time : 70000
     * message : null
     * levelConfuse : null
     * musicId : 65375555
     * accompany : null
     * sr : 44100
     * auEff : null
     * immerseType : null
     */

    private List<DataDTO> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public static class DataDTO {
        private long id;
        private String url;
        private int br;
        private int size;
        private String md5;
        private int code;
        private int expi;
        private String type;
        private double gain;
        private double peak;
        private double closedGain;
        private double closedPeak;
        private int fee;
        private String uf;
        private int payed;
        private int flag;
        private boolean canExtend;
        private FreeTrialInfoDTO freeTrialInfo;
        private String level;
        private String encodeType;
        private String channelLayout;
        /**
         * resConsumable : false
         * userConsumable : false
         * listenType : null
         * cannotListenReason : null
         * playReason : null
         * freeLimitTagType : null
         */

        private FreeTrialPrivilegeDTO freeTrialPrivilege;
        /**
         * resConsumable : false
         * userConsumable : false
         * type : 0
         * remainTime : 0
         */

        private FreeTimeTrialPrivilegeDTO freeTimeTrialPrivilege;
        private int urlSource;
        private int rightSource;
        private String podcastCtrp;
        private String effectTypes;
        private int time;
        private String message;
        private String levelConfuse;
        private String musicId;
        private String accompany;
        private int sr;
        private String auEff;
        private String immerseType;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getBr() {
            return br;
        }

        public void setBr(int br) {
            this.br = br;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getExpi() {
            return expi;
        }

        public void setExpi(int expi) {
            this.expi = expi;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getGain() {
            return gain;
        }

        public void setGain(double gain) {
            this.gain = gain;
        }

        public double getPeak() {
            return peak;
        }

        public void setPeak(double peak) {
            this.peak = peak;
        }

        public double getClosedGain() {
            return closedGain;
        }

        public void setClosedGain(double closedGain) {
            this.closedGain = closedGain;
        }

        public double getClosedPeak() {
            return closedPeak;
        }

        public void setClosedPeak(double closedPeak) {
            this.closedPeak = closedPeak;
        }

        public int getFee() {
            return fee;
        }

        public void setFee(int fee) {
            this.fee = fee;
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }

        public int getPayed() {
            return payed;
        }

        public void setPayed(int payed) {
            this.payed = payed;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public boolean isCanExtend() {
            return canExtend;
        }

        public void setCanExtend(boolean canExtend) {
            this.canExtend = canExtend;
        }

        public FreeTrialInfoDTO getFreeTrialInfo() {
            return freeTrialInfo;
        }

        public void setFreeTrialInfo(FreeTrialInfoDTO freeTrialInfo) {
            this.freeTrialInfo = freeTrialInfo;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getEncodeType() {
            return encodeType;
        }

        public void setEncodeType(String encodeType) {
            this.encodeType = encodeType;
        }

        public String getChannelLayout() {
            return channelLayout;
        }

        public void setChannelLayout(String channelLayout) {
            this.channelLayout = channelLayout;
        }

        public FreeTrialPrivilegeDTO getFreeTrialPrivilege() {
            return freeTrialPrivilege;
        }

        public void setFreeTrialPrivilege(FreeTrialPrivilegeDTO freeTrialPrivilege) {
            this.freeTrialPrivilege = freeTrialPrivilege;
        }

        public FreeTimeTrialPrivilegeDTO getFreeTimeTrialPrivilege() {
            return freeTimeTrialPrivilege;
        }

        public void setFreeTimeTrialPrivilege(FreeTimeTrialPrivilegeDTO freeTimeTrialPrivilege) {
            this.freeTimeTrialPrivilege = freeTimeTrialPrivilege;
        }

        public int getUrlSource() {
            return urlSource;
        }

        public void setUrlSource(int urlSource) {
            this.urlSource = urlSource;
        }

        public int getRightSource() {
            return rightSource;
        }

        public void setRightSource(int rightSource) {
            this.rightSource = rightSource;
        }

        public String getPodcastCtrp() {
            return podcastCtrp;
        }

        public void setPodcastCtrp(String podcastCtrp) {
            this.podcastCtrp = podcastCtrp;
        }

        public String getEffectTypes() {
            return effectTypes;
        }

        public void setEffectTypes(String effectTypes) {
            this.effectTypes = effectTypes;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getLevelConfuse() {
            return levelConfuse;
        }

        public void setLevelConfuse(String levelConfuse) {
            this.levelConfuse = levelConfuse;
        }

        public String getMusicId() {
            return musicId;
        }

        public void setMusicId(String musicId) {
            this.musicId = musicId;
        }

        public String getAccompany() {
            return accompany;
        }

        public void setAccompany(String accompany) {
            this.accompany = accompany;
        }

        public int getSr() {
            return sr;
        }

        public void setSr(int sr) {
            this.sr = sr;
        }

        public String getAuEff() {
            return auEff;
        }

        public void setAuEff(String auEff) {
            this.auEff = auEff;
        }

        public String getImmerseType() {
            return immerseType;
        }

        public void setImmerseType(String immerseType) {
            this.immerseType = immerseType;
        }

        public static class FreeTrialInfoDTO {
            private int fragmentType;
            private int start;
            private int end;
            /**
             * fragSource : default
             */

            private FreeTrialInfoDTO.AlgDataDTO algData;

            public int getFragmentType() {
                return fragmentType;
            }

            public void setFragmentType(int fragmentType) {
                this.fragmentType = fragmentType;
            }

            public int getStart() {
                return start;
            }

            public void setStart(int start) {
                this.start = start;
            }

            public int getEnd() {
                return end;
            }

            public void setEnd(int end) {
                this.end = end;
            }

            public AlgDataDTO getAlgData() {
                return algData;
            }

            public void setAlgData(AlgDataDTO algData) {
                this.algData = algData;
            }

            public static class AlgDataDTO {
                private String fragSource;

                public String getFragSource() {
                    return fragSource;
                }

                public void setFragSource(String fragSource) {
                    this.fragSource = fragSource;
                }
            }
        }
        public static class FreeTrialPrivilegeDTO {
            private boolean resConsumable;
            private boolean userConsumable;
            private String listenType;
            private String cannotListenReason;
            private String playReason;
            private String freeLimitTagType;

            public boolean isResConsumable() {
                return resConsumable;
            }

            public void setResConsumable(boolean resConsumable) {
                this.resConsumable = resConsumable;
            }

            public boolean isUserConsumable() {
                return userConsumable;
            }

            public void setUserConsumable(boolean userConsumable) {
                this.userConsumable = userConsumable;
            }

            public String getListenType() {
                return listenType;
            }

            public void setListenType(String listenType) {
                this.listenType = listenType;
            }

            public String getCannotListenReason() {
                return cannotListenReason;
            }

            public void setCannotListenReason(String cannotListenReason) {
                this.cannotListenReason = cannotListenReason;
            }

            public String getPlayReason() {
                return playReason;
            }

            public void setPlayReason(String playReason) {
                this.playReason = playReason;
            }

            public String getFreeLimitTagType() {
                return freeLimitTagType;
            }

            public void setFreeLimitTagType(String freeLimitTagType) {
                this.freeLimitTagType = freeLimitTagType;
            }
        }

        public static class FreeTimeTrialPrivilegeDTO {
            private boolean resConsumable;
            private boolean userConsumable;
            private int type;
            private int remainTime;

            public boolean isResConsumable() {
                return resConsumable;
            }

            public void setResConsumable(boolean resConsumable) {
                this.resConsumable = resConsumable;
            }

            public boolean isUserConsumable() {
                return userConsumable;
            }

            public void setUserConsumable(boolean userConsumable) {
                this.userConsumable = userConsumable;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getRemainTime() {
                return remainTime;
            }

            public void setRemainTime(int remainTime) {
                this.remainTime = remainTime;
            }
        }
    }
}

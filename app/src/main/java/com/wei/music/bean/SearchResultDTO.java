package com.wei.music.bean;

import java.util.List;

public class SearchResultDTO {

    /**
     * result
     */
    private ResultDTO result;
    /**
     * code
     */
    private Integer code;
    /**
     * trp
     */
    private TrpDTO trp;

    public ResultDTO getResult() {
        return result;
    }

    public void setResult(ResultDTO result) {
        this.result = result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public TrpDTO getTrp() {
        return trp;
    }

    public void setTrp(TrpDTO trp) {
        this.trp = trp;
    }

    public static class ResultDTO {
        /**
         * songs
         */
        private List<SongsDTO> songs;
        /**
         * hasMore
         */
        private Boolean hasMore;
        /**
         * songCount
         */
        private Integer songCount;

        public List<SongsDTO> getSongs() {
            return songs;
        }

        public void setSongs(List<SongsDTO> songs) {
            this.songs = songs;
        }

        public Boolean getHasMore() {
            return hasMore;
        }

        public void setHasMore(Boolean hasMore) {
            this.hasMore = hasMore;
        }

        public Integer getSongCount() {
            return songCount;
        }

        public void setSongCount(Integer songCount) {
            this.songCount = songCount;
        }

        public static class SongsDTO {
            /**
             * album
             */
            private AlbumDTO album;
            /**
             * fee
             */
            private Integer fee;
            /**
             * duration
             */
            private Integer duration;
            /**
             * rtype
             */
            private Integer rtype;
            /**
             * ftype
             */
            private Integer ftype;
            /**
             * artists
             */
            private List<ArtistsDTO> artists;
            /**
             * copyrightId
             */
            private Integer copyrightId;
            /**
             * mvid
             */
            private Integer mvid;
            /**
             * name
             */
            private String name;
            /**
             * alias
             */
            private List<?> alias;
            /**
             * id
             */
            private Long id;
            /**
             * mark
             */
            private Long mark;
            /**
             * status
             */
            private Integer status;
            /**
             * transNames
             */
            private List<String> transNames;

            public AlbumDTO getAlbum() {
                return album;
            }

            public void setAlbum(AlbumDTO album) {
                this.album = album;
            }

            public Integer getFee() {
                return fee;
            }

            public void setFee(Integer fee) {
                this.fee = fee;
            }

            public Integer getDuration() {
                return duration;
            }

            public void setDuration(Integer duration) {
                this.duration = duration;
            }

            public Integer getRtype() {
                return rtype;
            }

            public void setRtype(Integer rtype) {
                this.rtype = rtype;
            }

            public Integer getFtype() {
                return ftype;
            }

            public void setFtype(Integer ftype) {
                this.ftype = ftype;
            }

            public List<ArtistsDTO> getArtists() {
                return artists;
            }

            public void setArtists(List<ArtistsDTO> artists) {
                this.artists = artists;
            }

            public Integer getCopyrightId() {
                return copyrightId;
            }

            public void setCopyrightId(Integer copyrightId) {
                this.copyrightId = copyrightId;
            }

            public Integer getMvid() {
                return mvid;
            }

            public void setMvid(Integer mvid) {
                this.mvid = mvid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<?> getAlias() {
                return alias;
            }

            public void setAlias(List<?> alias) {
                this.alias = alias;
            }

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public Long getMark() {
                return mark;
            }

            public void setMark(Long mark) {
                this.mark = mark;
            }

            public Integer getStatus() {
                return status;
            }

            public void setStatus(Integer status) {
                this.status = status;
            }

            public List<String> getTransNames() {
                return transNames;
            }

            public void setTransNames(List<String> transNames) {
                this.transNames = transNames;
            }

            public static class AlbumDTO {
                /**
                 * publishTime
                 */
                private Long publishTime;
                /**
                 * size
                 */
                private Integer size;
                /**
                 * artist
                 */
                private ArtistDTO artist;
                /**
                 * copyrightId
                 */
                private Integer copyrightId;
                /**
                 * name
                 */
                private String name;
                /**
                 * id
                 */
                private Long id;
                /**
                 * picId
                 */
                private Long picId;
                /**
                 * mark
                 */
                private Integer mark;
                /**
                 * status
                 */
                private Integer status;

                public Long getPublishTime() {
                    return publishTime;
                }

                public void setPublishTime(Long publishTime) {
                    this.publishTime = publishTime;
                }

                public Integer getSize() {
                    return size;
                }

                public void setSize(Integer size) {
                    this.size = size;
                }

                public ArtistDTO getArtist() {
                    return artist;
                }

                public void setArtist(ArtistDTO artist) {
                    this.artist = artist;
                }

                public Integer getCopyrightId() {
                    return copyrightId;
                }

                public void setCopyrightId(Integer copyrightId) {
                    this.copyrightId = copyrightId;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public Long getId() {
                    return id;
                }

                public void setId(Long id) {
                    this.id = id;
                }

                public Long getPicId() {
                    return picId;
                }

                public void setPicId(Long picId) {
                    this.picId = picId;
                }

                public Integer getMark() {
                    return mark;
                }

                public void setMark(Integer mark) {
                    this.mark = mark;
                }

                public Integer getStatus() {
                    return status;
                }

                public void setStatus(Integer status) {
                    this.status = status;
                }

                public static class ArtistDTO {
                    /**
                     * img1v1Url
                     */
                    private String img1v1Url;
                    /**
                     * musicSize
                     */
                    private Integer musicSize;
                    /**
                     * albumSize
                     */
                    private Integer albumSize;
                    /**
                     * img1v1
                     */
                    private Integer img1v1;
                    /**
                     * name
                     */
                    private String name;
                    /**
                     * alias
                     */
                    private List<?> alias;
                    /**
                     * id
                     */
                    private Long id;
                    /**
                     * picId
                     */
                    private Integer picId;

                    public String getImg1v1Url() {
                        return img1v1Url;
                    }

                    public void setImg1v1Url(String img1v1Url) {
                        this.img1v1Url = img1v1Url;
                    }

                    public Integer getMusicSize() {
                        return musicSize;
                    }

                    public void setMusicSize(Integer musicSize) {
                        this.musicSize = musicSize;
                    }

                    public Integer getAlbumSize() {
                        return albumSize;
                    }

                    public void setAlbumSize(Integer albumSize) {
                        this.albumSize = albumSize;
                    }

                    public Integer getImg1v1() {
                        return img1v1;
                    }

                    public void setImg1v1(Integer img1v1) {
                        this.img1v1 = img1v1;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public List<?> getAlias() {
                        return alias;
                    }

                    public void setAlias(List<?> alias) {
                        this.alias = alias;
                    }

                    public Long getId() {
                        return id;
                    }

                    public void setId(Long id) {
                        this.id = id;
                    }

                    public Integer getPicId() {
                        return picId;
                    }

                    public void setPicId(Integer picId) {
                        this.picId = picId;
                    }
                }
            }

            public static class ArtistsDTO {
                /**
                 * img1v1Url
                 */
                private String img1v1Url;
                /**
                 * musicSize
                 */
                private Integer musicSize;
                /**
                 * albumSize
                 */
                private Integer albumSize;
                /**
                 * img1v1
                 */
                private Integer img1v1;
                /**
                 * name
                 */
                private String name;
                /**
                 * alias
                 */
                private List<?> alias;
                /**
                 * id
                 */
                private Long id;
                /**
                 * picId
                 */
                private Integer picId;

                public String getImg1v1Url() {
                    return img1v1Url;
                }

                public void setImg1v1Url(String img1v1Url) {
                    this.img1v1Url = img1v1Url;
                }

                public Integer getMusicSize() {
                    return musicSize;
                }

                public void setMusicSize(Integer musicSize) {
                    this.musicSize = musicSize;
                }

                public Integer getAlbumSize() {
                    return albumSize;
                }

                public void setAlbumSize(Integer albumSize) {
                    this.albumSize = albumSize;
                }

                public Integer getImg1v1() {
                    return img1v1;
                }

                public void setImg1v1(Integer img1v1) {
                    this.img1v1 = img1v1;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<?> getAlias() {
                    return alias;
                }

                public void setAlias(List<?> alias) {
                    this.alias = alias;
                }

                public Long getId() {
                    return id;
                }

                public void setId(Long id) {
                    this.id = id;
                }

                public Integer getPicId() {
                    return picId;
                }

                public void setPicId(Integer picId) {
                    this.picId = picId;
                }
            }
        }
    }

    public static class TrpDTO {
        /**
         * rules
         */
        private List<String> rules;

        public List<String> getRules() {
            return rules;
        }

        public void setRules(List<String> rules) {
            this.rules = rules;
        }
    }
}

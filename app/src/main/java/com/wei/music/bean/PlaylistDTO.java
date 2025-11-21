package com.wei.music.bean;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class PlaylistDTO {

    /**
     * subscribers : []
     * subscribed : null
     * creator : {"defaultAvatar":false,"province":360000,"authStatus":0,"followed":false,"avatarUrl":"http://p1.music.126.net/9vtd4DuacxJuNqersNVGlg==/109951164597268828.jpg","accountStatus":0,"gender":1,"city":360800,"birthday":0,"userId":1876523706,"userType":0,"nickname":"腐烂元素","signature":"","description":"","detailDescription":"","avatarImgId":109951164597268830,"backgroundImgId":109951162868126480,"backgroundUrl":"http://p1.music.126.net/_f8R60U9mZ42sSNvdPn2sQ==/109951162868126486.jpg","authority":0,"mutual":false,"expertTags":null,"experts":null,"djStatus":0,"vipType":0,"remarkName":null,"authenticationTypes":0,"avatarDetail":null,"anchor":false,"avatarImgIdStr":"109951164597268828","backgroundImgIdStr":"109951162868126486","avatarImgId_str":"109951164597268828"}
     * artists : null
     * tracks : null
     * top : false
     * updateFrequency : null
     * backgroundCoverId : 0
     * backgroundCoverUrl : null
     * titleImage : 0
     * titleImageUrl : null
     * englishTitle : null
     * opRecommend : false
     * recommendInfo : null
     * subscribedCount : 0
     * cloudTrackCount : 0
     * userId : 1876523706
     * totalDuration : 0
     * coverImgId : 109951166361455440
     * privacy : 0
     * trackUpdateTime : 1762809991250
     * trackCount : 173
     * updateTime : 1752088579270
     * commentThreadId : A_PL_0_2834579439
     * coverImgUrl : http://p1.music.126.net/mFJKHNEAF9qLv_-VQpZOTw==/109951166361455433.jpg
     * specialType : 5
     * anonimous : false
     * createTime : 1559988994443
     * highQuality : false
     * newImported : false
     * trackNumberUpdateTime : 1752088579270
     * playCount : 2094
     * adType : 0
     * description : null
     * tags : []
     * ordered : true
     * status : 0
     * name : 腐烂元素喜欢的音乐
     * id : 2834579439
     * coverImgId_str : 109951166361455433
     * sharedUsers : null
     * shareStatus : null
     * copied : false
     * containsTracks : false
     */
    private String subscribed;

    private CreatorDTO creator;
    private String artists;
    private String tracks;
    private boolean top;
    private String updateFrequency;
    private int backgroundCoverId;
    private String backgroundCoverUrl;
    private int titleImage;
    private String titleImageUrl;
    private String englishTitle;
    private boolean opRecommend;
    private String recommendInfo;
    private int subscribedCount;
    private int cloudTrackCount;
    private int userId;
    private int totalDuration;
    private long coverImgId;
    private int privacy;
    private long trackUpdateTime;
    private int trackCount;
    private long updateTime;
    private String commentThreadId;
    private String coverImgUrl;
    private int specialType;
    private boolean anonimous;
    private long createTime;
    private boolean highQuality;
    private boolean newImported;
    private long trackNumberUpdateTime;
    private int playCount;
    private int adType;
    private String description;
    private boolean ordered;
    private int status;
    private String name;

    @PrimaryKey
    private long id;
    private String coverImgId_str;
    private String sharedUsers;
    private String shareStatus;
    private boolean copied;
    private boolean containsTracks;
    @Ignore
    private List<String> subscribers;
    @Ignore
    private List<String> tags;


    public CreatorDTO getCreator() {
        return creator;
    }

    public void setCreator(CreatorDTO creator) {
        this.creator = creator;
    }



    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }


    public int getBackgroundCoverId() {
        return backgroundCoverId;
    }

    public void setBackgroundCoverId(int backgroundCoverId) {
        this.backgroundCoverId = backgroundCoverId;
    }



    public int getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(int titleImage) {
        this.titleImage = titleImage;
    }


    public boolean isOpRecommend() {
        return opRecommend;
    }

    public void setOpRecommend(boolean opRecommend) {
        this.opRecommend = opRecommend;
    }


    public int getSubscribedCount() {
        return subscribedCount;
    }

    public void setSubscribedCount(int subscribedCount) {
        this.subscribedCount = subscribedCount;
    }

    public int getCloudTrackCount() {
        return cloudTrackCount;
    }

    public void setCloudTrackCount(int cloudTrackCount) {
        this.cloudTrackCount = cloudTrackCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public long getCoverImgId() {
        return coverImgId;
    }

    public void setCoverImgId(long coverImgId) {
        this.coverImgId = coverImgId;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public long getTrackUpdateTime() {
        return trackUpdateTime;
    }

    public void setTrackUpdateTime(long trackUpdateTime) {
        this.trackUpdateTime = trackUpdateTime;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCommentThreadId() {
        return commentThreadId;
    }

    public void setCommentThreadId(String commentThreadId) {
        this.commentThreadId = commentThreadId;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public int getSpecialType() {
        return specialType;
    }

    public void setSpecialType(int specialType) {
        this.specialType = specialType;
    }

    public boolean isAnonimous() {
        return anonimous;
    }

    public void setAnonimous(boolean anonimous) {
        this.anonimous = anonimous;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isHighQuality() {
        return highQuality;
    }

    public void setHighQuality(boolean highQuality) {
        this.highQuality = highQuality;
    }

    public boolean isNewImported() {
        return newImported;
    }

    public void setNewImported(boolean newImported) {
        this.newImported = newImported;
    }

    public long getTrackNumberUpdateTime() {
        return trackNumberUpdateTime;
    }

    public void setTrackNumberUpdateTime(long trackNumberUpdateTime) {
        this.trackNumberUpdateTime = trackNumberUpdateTime;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }


    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCoverImgId_str() {
        return coverImgId_str;
    }

    public void setCoverImgId_str(String coverImgId_str) {
        this.coverImgId_str = coverImgId_str;
    }


    public boolean isCopied() {
        return copied;
    }

    public void setCopied(boolean copied) {
        this.copied = copied;
    }

    public boolean isContainsTracks() {
        return containsTracks;
    }

    public void setContainsTracks(boolean containsTracks) {
        this.containsTracks = containsTracks;
    }


    public String getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(String subscribed) {
        this.subscribed = subscribed;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getTracks() {
        return tracks;
    }

    public void setTracks(String tracks) {
        this.tracks = tracks;
    }

    public String getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(String updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public String getBackgroundCoverUrl() {
        return backgroundCoverUrl;
    }

    public void setBackgroundCoverUrl(String backgroundCoverUrl) {
        this.backgroundCoverUrl = backgroundCoverUrl;
    }

    public String getTitleImageUrl() {
        return titleImageUrl;
    }

    public void setTitleImageUrl(String titleImageUrl) {
        this.titleImageUrl = titleImageUrl;
    }

    public String getEnglishTitle() {
        return englishTitle;
    }

    public void setEnglishTitle(String englishTitle) {
        this.englishTitle = englishTitle;
    }

    public String getRecommendInfo() {
        return recommendInfo;
    }

    public void setRecommendInfo(String recommendInfo) {
        this.recommendInfo = recommendInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(String sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

    public String getShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(String shareStatus) {
        this.shareStatus = shareStatus;
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<String> subscribers) {
        this.subscribers = subscribers;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
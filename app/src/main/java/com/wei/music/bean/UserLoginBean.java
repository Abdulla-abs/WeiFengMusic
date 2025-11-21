package com.wei.music.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class UserLoginBean {


    @PrimaryKey
    private int roomId;
    /**
     * loginType : 1
     * hitType : 0
     * code : 200
     * account : {"id":1876523706,"userName":"1_19138680886","type":1,"status":0,"whitelistAuthority":0,"createTime":1559988950886,"salt":"[B@9923529","tokenVersion":1,"ban":0,"baoyueVersion":0,"donateVersion":0,"vipType":0,"viptypeVersion":1736526757767,"anonimousUser":false,"uninitialized":false}
     * token : 004E2027D6E5B5AEAC0E3F605BFF6908BB0C3791174117BE254B9845A353505FC0EAE479B92A17E90AE587724ECF3ED46F0314DD5E5C8DBE16C03DC1284682DA76787A91A56D55CCF24096722A35B538A5C43DFEDB1312562F5EC4449B39422C737F5546D6E406085ABB353F9E205EB15EA9119293B3D8C3EAF62FD5C5F3E34FEDE5E54A6BAA94FF36A870606F8FB2C96E7BAE94450B9B4FAD4797F010F621C3A2A3F5A3D9E006FF5ABE6E3810291B69DAE29690FDEB447483CC0B6C5848B4D7320CC732697ECA2BB26258B5897A1864056FF928D4C34D9FAE55B0A77728BB30A3EFAC326F721B7E1FB15A7ACA4640432B0E12CC69B0B34BE137CADC1E37270C0004C689C0CA92700B61E403C5775A9D3CE2F1AF1F639B42EF4C22F2E6D3CAE9044059B053DCB7F157FB33DA47698888DFD775E2D3F1D87000D7EAB52EB65800C9DDE023EA5CEB98616A28DBC01A08F937
     * profile : {"followed":false,"backgroundUrl":"https://p2.music.126.net/_f8R60U9mZ42sSNvdPn2sQ==/109951162868126486.jpg","avatarImgIdStr":"109951164597268828","backgroundImgIdStr":"109951162868126486","userType":0,"avatarUrl":"https://p2.music.126.net/9vtd4DuacxJuNqersNVGlg==/109951164597268828.jpg","vipType":0,"authStatus":0,"djStatus":0,"detailDescription":"","experts":{},"expertTags":null,"accountStatus":0,"nickname":"腐烂元素","birthday":-2209017600000,"gender":1,"province":360000,"city":360800,"avatarImgId":109951164597268830,"backgroundImgId":109951162868126480,"defaultAvatar":false,"mutual":false,"remarkName":null,"description":"","userId":1876523706,"signature":"","authority":0,"avatarImgId_str":"109951164597268828","followeds":2,"follows":30,"eventCount":0,"avatarDetail":null,"playlistCount":4,"playlistBeSubscribedCount":0}
     * bindings : [{"bindingTime":1753867150238,"refreshTime":1753867150,"tokenJsonStr":"{\"countrycode\":\"\",\"cellphone\":\"19138680886\",\"hasPassword\":true}","expiresIn":2147483647,"url":"","expired":false,"userId":1876523706,"id":16825899216,"type":1},{"bindingTime":1559988951129,"refreshTime":1711638774,"tokenJsonStr":"{\"access_token\":\"CB98F4573F89E0EB6202C8881C2571AF\",\"unionid\":\"UID_9C0D4EB48C4F8DEC9670EFFF08B01283\",\"openid\":\"D5D7B7AADB105480C95C9700CCD84EDA\",\"query_authority_cost\":0,\"nickname\":\"人类高质量空指针\",\"partnerType\":\"0\",\"expires_in\":7776000,\"login_cost\":19,\"expires_time\":1719414772015,\"authority_cost\":1679}","expiresIn":7776000,"url":"","expired":true,"userId":1876523706,"id":6893093387,"type":5}]
     * cookie : MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/openapi/clientlog; HTTPOnly;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/weapi/clientlog; HTTPOnly;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/neapi/clientlog; HTTPOnly;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/api/feedback; HTTPOnly;__remember_me=true; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/; HTTPOnly;MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/eapi/feedback; HTTPOnly;NMTID=00OXQJ4EGPAWNTb90b0gGE12_UXpIgAAAGZ3861uQ; Max-Age=315360000; Expires=Thu, 11 Oct 2035 23:01:30 GMT; Path=/;;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/openapi/clientlog; HTTPOnly;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/eapi/feedback; HTTPOnly;__csrf=1c7e05e5b14680b701c1ef0af4be4fa8; Max-Age=1296010; Expires=Tue, 28 Oct 2025 23:01:40 GMT; Path=/;;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/weapi/feedback; HTTPOnly;MUSIC_R_T=; Max-Age=0; Expires=Mon, 13 Oct 2025 23:01:30 GMT; Path=/;;MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT;  Path=/wapi/clientlog; HTTPOnly;MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/neapi/feedback; HTTPOnly;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/eapi/clientlog; HTTPOnly;MUSIC_SNS=; Max-Age=0; Expires=Mon, 13 Oct 2025 23:01:30 GMT; Path=/;MUSIC_A_T=; Max-Age=0; Expires=Mon, 13 Oct 2025 23:01:30 GMT; Path=/;;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/api/clientlog; HTTPOnly;MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/neapi/clientlog; HTTPOnly;MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/api/clientlog; HTTPOnly;MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/weapi/clientlog; HTTPOnly;MUSIC_U=004E2027D6E5B5AEAC0E3F605BFF6908BB0C3791174117BE254B9845A353505FC0EAE479B92A17E90AE587724ECF3ED46F0314DD5E5C8DBE16C03DC1284682DA76787A91A56D55CCF24096722A35B538A5C43DFEDB1312562F5EC4449B39422C737F5546D6E406085ABB353F9E205EB15EA9119293B3D8C3EAF62FD5C5F3E34FEDE5E54A6BAA94FF36A870606F8FB2C96E7BAE94450B9B4FAD4797F010F621C3A2A3F5A3D9E006FF5ABE6E3810291B69DAE29690FDEB447483CC0B6C5848B4D7320CC732697ECA2BB26258B5897A1864056FF928D4C34D9FAE55B0A77728BB30A3EFAC326F721B7E1FB15A7ACA4640432B0E12CC69B0B34BE137CADC1E37270C0004C689C0CA92700B61E403C5775A9D3CE2F1AF1F639B42EF4C22F2E6D3CAE9044059B053DCB7F157FB33DA47698888DFD775E2D3F1D87000D7EAB52EB65800C9DDE023EA5CEB98616A28DBC01A08F937; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/; HTTPOnly;MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/api/feedback; HTTPOnly;MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/wapi/feedback; HTTPOnly;MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/eapi/clientlog; HTTPOnly;MUSIC_R_T=1559988994318; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/weapi/feedback; HTTPOnly;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/neapi/feedback; HTTPOnly;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/wapi/feedback; HTTPOnly;MUSIC_A_T=1559988950886; Max-Age=2147483647; Expires=Sun, 01 Nov 2093 02:15:37 GMT; Path=/wapi/clientlog; HTTPOnly
     */

    private int loginType;
    private int hitType;
    private int code;
    /**
     * id : 1876523706
     * userName : 1_19138680886
     * type : 1
     * status : 0
     * whitelistAuthority : 0
     * createTime : 1559988950886
     * salt : [B@9923529
     * tokenVersion : 1
     * ban : 0
     * baoyueVersion : 0
     * donateVersion : 0
     * vipType : 0
     * viptypeVersion : 1736526757767
     * anonimousUser : false
     * uninitialized : false
     */

    private AccountDTO account;
    private String token;
    /**
     * followed : false
     * backgroundUrl : https://p2.music.126.net/_f8R60U9mZ42sSNvdPn2sQ==/109951162868126486.jpg
     * avatarImgIdStr : 109951164597268828
     * backgroundImgIdStr : 109951162868126486
     * userType : 0
     * avatarUrl : https://p2.music.126.net/9vtd4DuacxJuNqersNVGlg==/109951164597268828.jpg
     * vipType : 0
     * authStatus : 0
     * djStatus : 0
     * detailDescription :
     * experts : {}
     * expertTags : null
     * accountStatus : 0
     * nickname : 腐烂元素
     * birthday : -2209017600000
     * gender : 1
     * province : 360000
     * city : 360800
     * avatarImgId : 109951164597268830
     * backgroundImgId : 109951162868126480
     * defaultAvatar : false
     * mutual : false
     * remarkName : null
     * description :
     * userId : 1876523706
     * signature :
     * authority : 0
     * avatarImgId_str : 109951164597268828
     * followeds : 2
     * follows : 30
     * eventCount : 0
     * avatarDetail : null
     * playlistCount : 4
     * playlistBeSubscribedCount : 0
     */

    private ProfileDTO profile;
    private String cookie;
    /**
     * bindingTime : 1753867150238
     * refreshTime : 1753867150
     * tokenJsonStr : {"countrycode":"","cellphone":"19138680886","hasPassword":true}
     * expiresIn : 2147483647
     * url :
     * expired : false
     * userId : 1876523706
     * id : 16825899216
     * type : 1
     */

    private List<BindingsDTO> bindings;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public int getHitType() {
        return hitType;
    }

    public void setHitType(int hitType) {
        this.hitType = hitType;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AccountDTO getAccount() {
        return account;
    }

    public void setAccount(AccountDTO account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
        this.profile = profile;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public List<BindingsDTO> getBindings() {
        return bindings;
    }

    public void setBindings(List<BindingsDTO> bindings) {
        this.bindings = bindings;
    }

}

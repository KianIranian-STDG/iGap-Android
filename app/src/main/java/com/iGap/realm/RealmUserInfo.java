package com.iGap.realm;

import io.realm.RealmObject;

public class RealmUserInfo extends RealmObject {

    private boolean needRegistration;
    private long userId;
    private String userName;
    private String countryISOCode;
    private String email;
    private String nickName;
    private String gender;
    private String phoneNumber;
    private String avatarPath;
    private String token;

    //===Setters

    public void setUserRegistrationState(boolean value) {
        this.needRegistration = value;
    }

    public void setUserId(long value) {
        this.userId = value;
    }

    public void setUserName(String value) {
        this.userName = value;
    }

    public void setCountryISOCode(String value) {
        this.countryISOCode = value;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public void setNickName(String value) {
        this.nickName = value;
    }

    public void setGender(String value) {
        this.gender = value;
    }

    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    public void setAvatarPath(String value) {
        this.avatarPath = value;
    }

    public void setToken(String value) {
        this.token = value;
    }

    //===Getters

    public boolean getUserRegistrationState() {
        return this.needRegistration;
    }

    public long getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getCountryISOCode() {
        return this.countryISOCode;
    }

    public String getEmail() {
        return this.email;
    }

    public String getNickName() {
        return this.nickName;
    }

    public String getGender() {
        return this.gender;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getAvatarPath() {
        return this.avatarPath;
    }

    public String getToken() {
        return this.token;
    }

}

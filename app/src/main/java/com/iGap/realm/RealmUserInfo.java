package com.iGap.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmUserInfo extends RealmObject {

    @PrimaryKey private long userId;
    private boolean registrationStatus;
    private String userName;
    private String countryISOCode;
    private String email;
    private String nickName;
    private String gender;
    private String phoneNumber;
    private String initials;
    private String color;

    private RealmList<RealmAvatarPath> avatarPath = new RealmList<>();
    private RealmList<RealmAvatarToken> avatarTokens = new RealmList<>();
    private String token;

    public RealmList<RealmAvatarToken> getAvatarTokens() {
        return avatarTokens;
    }

    public void setAvatarTokens(RealmList<RealmAvatarToken> avatarTokens) {
        this.avatarTokens = avatarTokens;
    }

    public RealmList<RealmAvatarPath> getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(RealmList<RealmAvatarPath> avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void addAvatarPath(RealmAvatarPath avatarPath) {
        this.avatarPath.add(avatarPath);
    }

    public void addAvatarToken(RealmAvatarToken avatarToken) {
        this.avatarTokens.add(avatarToken);
    }

    public boolean getUserRegistrationState() {
        return this.registrationStatus;
    }

    public void setUserRegistrationState(boolean value) {
        this.registrationStatus = value;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long value) {
        this.userId = value;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String value) {
        this.userName = value;
    }

    public String getCountryISOCode() {
        return this.countryISOCode;
    }

    public void setCountryISOCode(String value) {
        this.countryISOCode = value;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public String getNickName() {
        return this.nickName;
    }
    //===Getters

    public void setNickName(String value) {
        this.nickName = value;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String value) {
        this.gender = value;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String value) {
        this.token = value;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

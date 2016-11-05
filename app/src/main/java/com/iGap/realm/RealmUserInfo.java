package com.iGap.realm;

import io.realm.RealmObject;

public class RealmUserInfo extends RealmObject {

    private RealmRegisteredInfo userInfo;
    private boolean registrationStatus;
    private String email;
    private String gender;
    private int selfRemove;
    private String token;

    public RealmRegisteredInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(RealmRegisteredInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean getUserRegistrationState() {
        return this.registrationStatus;
    }

    public void setUserRegistrationState(boolean value) {
        this.registrationStatus = value;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String value) {
        this.gender = value;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String value) {
        this.token = value;
    }

    public int getSelfRemove() {
        return selfRemove;
    }

    public void setSelfRemove(int selfRemove) {
        this.selfRemove = selfRemove;
    }

    public long getUserId() {
        return this.userInfo.getId();
    }
}

package com.iGap.realm;

import io.realm.RealmObject;

public class UserInfo extends RealmObject {

    private boolean needRegistration;
    private boolean needLogin;
    private String name;
    private String countryCode;
    private String email;
    private String nickName;
    private String phoneNumber;

    //===Setters
    public void setUserRegistrationState(boolean value) {
        this.needRegistration = value;
    }

    public void setUserLoginState(boolean value) {
        this.needLogin = value;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public void setNickName(String value) {
        this.nickName = value;
    }

    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    //===Getters
    public boolean getUserRegistrationState() {
        return this.needRegistration;
    }

    public boolean getUserLoginState() {
        return this.needLogin;
    }

    public String getName() {
        return this.name;
    }

}

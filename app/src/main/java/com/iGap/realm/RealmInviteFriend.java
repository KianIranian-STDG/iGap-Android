package com.iGap.realm;

import io.realm.RealmObject;

/**
 * Created by android3 on 9/14/2016.
 */
public class RealmInviteFriend extends RealmObject {

    private String phone;
    private String firstName;
    private String lastName;
    private String displayName;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

package com.iGap.realm;

import io.realm.RealmObject;

/**
 * Created by Rahmani on 8/29/2016.
 */
public class RealmUserContactsGetListResponse extends RealmObject {

    private long id;
    private String username;
    private long phone;
    private String first_name;
    private String last_name;
    private String display_name;
    private String initials;
    private String color;
    private long last_seen;
//    private Enum status ;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
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

    public long getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(long last_seen) {
        this.last_seen = last_seen;
    }

//    public Enum getStatus() {
//        return status;
//    }
//
//    public void setStatus(Enum status) {
//        this.status = status;
//    }
}

package com.iGap.module;

import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmRoomMessageContact;

public class StructRegisteredInfo {
    public long id;
    public String username;
    public String phone;
    public String firstName;
    public String lastName;
    public String displayName;
    public String initials;
    public String color;
    public String status;
    public int lastSeen;
    public int avatarCount;
    public RealmAvatar avatar;

    public StructRegisteredInfo() {
    }

    public StructRegisteredInfo(long id, String username, String phone, String firstName, String lastName, String displayName, String initials, String color, String status, int lastSeen, int avatarCount, RealmAvatar avatar) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.initials = initials;
        this.color = color;
        this.status = status;
        this.lastSeen = lastSeen;
        this.avatarCount = avatarCount;
        this.avatar = avatar;
    }

    public StructRegisteredInfo(String lastName, String firstName, String phone, String username, long id) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.phone = phone;
        this.username = username;
        this.id = id;
    }

    public static StructRegisteredInfo build(RealmRoomMessageContact messageContact) {
        if (messageContact == null) {
            return null;
        }
        StructRegisteredInfo userInfo = new StructRegisteredInfo();
        //userInfo.imageSource=;
        userInfo.firstName = messageContact.getFirstName();
        userInfo.lastName = messageContact.getLastName();
        userInfo.phone = messageContact.getPhones().get(0).getPhone();
        userInfo.displayName = userInfo.firstName + " " + userInfo.lastName;
        //userInfo.userName=;

        return userInfo;
    }
}

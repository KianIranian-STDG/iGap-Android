package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessageContact;

public class StructRegisteredInfo implements Parcelable {
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
    public StructMessageAttachment avatar;

    public StructRegisteredInfo() {
    }

    public StructRegisteredInfo(String lastName, String firstName, String phone, String username, long id) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.displayName = firstName + " " + lastName;
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
        if (messageContact.getPhones() != null && messageContact.getPhones().first() != null) {
            userInfo.phone = messageContact.getPhones().first().getString();
        }
        userInfo.displayName = userInfo.firstName + " " + userInfo.lastName;
        //userInfo.userName=;

        return userInfo;
    }

    public static StructRegisteredInfo build(ProtoGlobal.RoomMessageContact messageContact) {
        if (messageContact == null) {
            return null;
        }
        StructRegisteredInfo userInfo = new StructRegisteredInfo();
        //userInfo.imageSource=;
        userInfo.firstName = messageContact.getFirstName();
        userInfo.lastName = messageContact.getLastName();
        if (messageContact.getPhoneList() != null && messageContact.getPhoneList().size() > 0) {
            userInfo.phone = messageContact.getPhoneList().get(messageContact.getPhoneList().size() - 1);
        }
        userInfo.displayName = userInfo.firstName + " " + userInfo.lastName;
        //userInfo.userName=;

        return userInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.username);
        dest.writeString(this.phone);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.displayName);
        dest.writeString(this.initials);
        dest.writeString(this.color);
        dest.writeString(this.status);
        dest.writeInt(this.lastSeen);
        dest.writeInt(this.avatarCount);
        dest.writeParcelable(this.avatar, flags);
    }

    protected StructRegisteredInfo(Parcel in) {
        this.id = in.readLong();
        this.username = in.readString();
        this.phone = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.displayName = in.readString();
        this.initials = in.readString();
        this.color = in.readString();
        this.status = in.readString();
        this.lastSeen = in.readInt();
        this.avatarCount = in.readInt();
        this.avatar = in.readParcelable(StructMessageAttachment.class.getClassLoader());
    }

    public static final Parcelable.Creator<StructRegisteredInfo> CREATOR = new Parcelable.Creator<StructRegisteredInfo>() {
        @Override
        public StructRegisteredInfo createFromParcel(Parcel source) {
            return new StructRegisteredInfo(source);
        }

        @Override
        public StructRegisteredInfo[] newArray(int size) {
            return new StructRegisteredInfo[size];
        }
    };
}

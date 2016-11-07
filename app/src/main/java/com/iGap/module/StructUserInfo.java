package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.iGap.realm.RealmRoomMessageContact;

public class StructUserInfo implements Parcelable {

    public static final Parcelable.Creator<StructUserInfo> CREATOR =
            new Parcelable.Creator<StructUserInfo>() {
                @Override
                public StructUserInfo createFromParcel(Parcel source) {
                    return new StructUserInfo(source);
                }

                @Override
                public StructUserInfo[] newArray(int size) {
                    return new StructUserInfo[size];
                }
            };
    public String imageSource;
    public String userName;
    public String lastSeen;
    public String name;
    public String number;

    public StructUserInfo(String imageSource, String userName, String lastSeen, String name,
                          String number) {
        this.imageSource = imageSource;
        this.userName = userName;
        this.lastSeen = lastSeen;
        this.name = name;
        this.number = number;
    }

    public StructUserInfo() {
    }

    public StructUserInfo(String imageSource, String userName, String name, String number) {
        this.imageSource = imageSource;
        this.userName = userName;
        this.name = name;
        this.number = number;
    }

    protected StructUserInfo(Parcel in) {
        this.imageSource = in.readString();
        this.userName = in.readString();
        this.lastSeen = in.readString();
        this.name = in.readString();
        this.number = in.readString();
    }

    public static StructUserInfo build(RealmRoomMessageContact messageContact) {
        if (messageContact == null) {
            return null;
        }
        StructUserInfo userInfo = new StructUserInfo();
        //userInfo.imageSource=;
        userInfo.name = messageContact.getFirstName() + " " + messageContact.getLastName();
        //userInfo.lastSeen=;
        userInfo.number = messageContact.getPhones().get(0).getString();
        //userInfo.userName=;

        return userInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageSource);
        dest.writeString(this.userName);
        dest.writeString(this.lastSeen);
        dest.writeString(this.name);
        dest.writeString(this.number);
    }
}

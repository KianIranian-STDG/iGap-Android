package net.iGap.module.webrtc;

import android.graphics.Bitmap;

public class CallerInfo {
    public String name = "";
    public String lastName = "";
    public long userId;
    public Bitmap avatar;
    public String color = "";

    public long getUserId() {
        return userId;
    }

    public String getColor() {
        return color;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }
}

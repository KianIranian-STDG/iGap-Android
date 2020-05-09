package net.iGap.module.webrtc;

import android.graphics.Bitmap;

public class CallerInfo {
    public String name;
    public String lastName;
    public long userId;
    public Bitmap avatar;

    public long getUserId() {
        return userId;
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

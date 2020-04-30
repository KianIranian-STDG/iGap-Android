package net.iGap.module.webrtc;

import android.graphics.Bitmap;

public class CallerInfo {
    public String name;
    public String lastName;
    public Bitmap avatar;

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

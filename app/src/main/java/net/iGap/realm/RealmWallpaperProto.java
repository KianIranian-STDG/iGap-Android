package net.iGap.realm;

import java.io.File;

import io.realm.RealmObject;

public class RealmWallpaperProto extends RealmObject {

    private RealmAttachment File;
    private String Color;

    public RealmAttachment getFile() {
        return File;
    }

    public void setFile(RealmAttachment file) {
        File = file;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }
}

package net.iGap.module;

import net.iGap.fragments.FragmentChatBackground;
import net.iGap.realm.RealmWallpaperProto;

public class StructWallpaper {

    private FragmentChatBackground.WallpaperType wallpaperType;
    private String path;
    private RealmWallpaperProto protoWallpaper;


    public FragmentChatBackground.WallpaperType getWallpaperType() {
        return wallpaperType;
    }

    public void setWallpaperType(FragmentChatBackground.WallpaperType wallpaperType) {
        this.wallpaperType = wallpaperType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RealmWallpaperProto getProtoWallpaper() {
        return protoWallpaper;
    }

    public void setProtoWallpaper(RealmWallpaperProto mProtoWallpaper) {

        this.protoWallpaper = mProtoWallpaper;

    }
}

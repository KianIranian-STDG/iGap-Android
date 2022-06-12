package net.iGap.module;

import net.iGap.messenger.ui.fragments.ChatBackgroundFragment;
import net.iGap.realm.RealmWallpaperProto;

public class StructWallpaper {

    private ChatBackgroundFragment.WallpaperType wallpaperType;
    private String path;
    private RealmWallpaperProto protoWallpaper;


    public ChatBackgroundFragment.WallpaperType getWallpaperType() {
        return wallpaperType;
    }

    public void setWallpaperType(ChatBackgroundFragment.WallpaperType wallpaperType) {
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

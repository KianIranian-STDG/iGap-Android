package com.iGap.module;

import com.iGap.realm.RealmChannelExtra;

import org.parceler.Parcel;

@Parcel
public class StructChannelExtra {

    public String signature = "";
    public String viewsLabel = "1";
    public String thumbsUp = "0";
    public String thumbsDown = "0";

    public static StructChannelExtra convert(RealmChannelExtra realmChannelExtra) {
        StructChannelExtra structChannelExtra = new StructChannelExtra();
        structChannelExtra.signature = realmChannelExtra.getSignature();
        structChannelExtra.thumbsUp = realmChannelExtra.getThumbsUp();
        structChannelExtra.thumbsDown = realmChannelExtra.getThumbsDown();
        structChannelExtra.viewsLabel = realmChannelExtra.getViewsLabel();
        return structChannelExtra;
    }
}

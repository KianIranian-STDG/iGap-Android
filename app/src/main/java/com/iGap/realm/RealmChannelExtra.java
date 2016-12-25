package com.iGap.realm;

import com.iGap.module.StructChannelExtra;

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmChannelExtraRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = {RealmChannelExtraRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {RealmChannelExtra.class})
public class RealmChannelExtra extends RealmObject {

    private String signature;
    private String viewsLabel;
    private String thumbsUp;
    private String thumbsDown;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getViewsLabel() {
        return viewsLabel;
    }

    public void setViewsLabel(String viewsLabel) {
        this.viewsLabel = viewsLabel;
    }

    public String getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(String thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getThumbsDown() {
        return thumbsDown;
    }

    public void setThumbsDown(String thumbsDown) {
        this.thumbsDown = thumbsDown;
    }


    public static RealmChannelExtra convert(StructChannelExtra structChannelExtra) {
        Realm realm = Realm.getDefaultInstance();
        RealmChannelExtra realmChannelExtra = realm.createObject(RealmChannelExtra.class);
        realmChannelExtra.setSignature(structChannelExtra.signature);
        realmChannelExtra.setThumbsUp(structChannelExtra.thumbsUp);
        realmChannelExtra.setThumbsDown(structChannelExtra.thumbsDown);
        realmChannelExtra.setViewsLabel(structChannelExtra.viewsLabel);
        realm.close();
        return null;
    }
}

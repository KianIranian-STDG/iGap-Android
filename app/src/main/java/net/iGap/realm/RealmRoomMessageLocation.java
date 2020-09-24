/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.net_iGap_realm_RealmRoomMessageLocationRealmProxy;

@Parcel(implementations = {net_iGap_realm_RealmRoomMessageLocationRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmRoomMessageLocation.class})
public class RealmRoomMessageLocation extends RealmObject {

    @PrimaryKey
    private long id;
    private double locationLat;
    private double locationLong;
    private String imagePath; // this field not important now, because we use from 'locationLat' and 'locationLat' params for detect location path

    public static RealmRoomMessageLocation put(Realm realm, final ProtoGlobal.RoomMessageLocation input, Long id) {
        RealmRoomMessageLocation messageLocation = null;
        if (id != null) {
            messageLocation = realm.where(RealmRoomMessageLocation.class).equalTo("id", id).findFirst();
        }
        if (messageLocation == null) {
            messageLocation = realm.createObject(RealmRoomMessageLocation.class, AppUtils.makeRandomId());
        }
        messageLocation.setLocationLat(input.getLat());
        messageLocation.setLocationLong(input.getLon());

        return messageLocation;
    }

    public static RealmRoomMessageLocation put(Realm realm, double latitude, double longitude, String imagePath) {
        RealmRoomMessageLocation messageLocation = realm.createObject(RealmRoomMessageLocation.class, AppUtils.makeRandomId());
        messageLocation.setLocationLat(latitude);
        messageLocation.setLocationLong(longitude);
        messageLocation.setImagePath(imagePath);
        return messageLocation;
    }

    @Override
    public String toString() {
        return getLocationLat() + "," + getLocationLong();
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(double locationLong) {
        this.locationLong = locationLong;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

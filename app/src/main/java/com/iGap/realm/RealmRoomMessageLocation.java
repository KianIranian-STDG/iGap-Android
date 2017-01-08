package com.iGap.realm;

import com.iGap.module.SUID;
import com.iGap.proto.ProtoGlobal;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmRoomMessageLocationRealmProxy;
import io.realm.annotations.PrimaryKey;
import org.parceler.Parcel;

@Parcel(implementations = {RealmRoomMessageLocationRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {RealmRoomMessageLocation.class})
public class RealmRoomMessageLocation extends RealmObject {
    private double locationLat;
    private double locationLong;
    private String imagePath;
    @PrimaryKey
    private long id;

    public static RealmRoomMessageLocation build(final ProtoGlobal.RoomMessageLocation input) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessageLocation messageLocation = realm.createObject(RealmRoomMessageLocation.class, SUID.id().get());
        messageLocation.setLocationLat(input.getLat());
        messageLocation.setLocationLong(input.getLon());
        realm.close();

        return messageLocation;
    }

    @Override
    public String toString() {
        return Double.toString(getLocationLat()) + "," + Double.toString(getLocationLong());
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

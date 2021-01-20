package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageLocation;

public class LocationObject {
    public double lat;
    public double lan;

    public static LocationObject create(ProtoGlobal.RoomMessageLocation location) {
        if (location == null) {
            return null;
        }
        LocationObject locationObject = new LocationObject();

        locationObject.lan = location.getLon();
        locationObject.lat = location.getLat();

        return locationObject;
    }

    public static LocationObject create(RealmRoomMessageLocation realmLocation) {
        if (realmLocation == null) {
            return null;
        }
        LocationObject locationObject = new LocationObject();

        locationObject.lan = realmLocation.getLocationLong();
        locationObject.lat = realmLocation.getLocationLat();

        return locationObject;

    }
}

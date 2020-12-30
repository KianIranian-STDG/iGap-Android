package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;

public class LocationObject {
    public double lat;
    public double lan;

    public static LocationObject create(ProtoGlobal.RoomMessageLocation location) {
        LocationObject locationObject = new LocationObject();

        locationObject.lan = location.getLat();
        locationObject.lat = location.getLon();

        return locationObject;
    }
}

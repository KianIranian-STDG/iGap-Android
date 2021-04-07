package net.iGap.structs;

import net.iGap.realm.RealmRoomMessage;

public class AdditionalObject {
    public String data;
    public int type;
    public long id;

    public static AdditionalObject create(RealmRoomMessage realmRoomMessage) {
        AdditionalObject additionalObject = new AdditionalObject();

        additionalObject.data = realmRoomMessage.getRealmAdditional().getAdditionalData();
        additionalObject.type = realmRoomMessage.getRealmAdditional().getAdditionalType();
        additionalObject.id = realmRoomMessage.getRealmAdditional().getId();

        return additionalObject;
    }
}

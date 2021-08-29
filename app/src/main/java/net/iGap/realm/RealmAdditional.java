package net.iGap.realm;

import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmAdditional extends RealmObject {

    @PrimaryKey
    private long id;
    private String additionalData;
    private int AdditionalType;


    public static RealmAdditional put(Realm realm, final ProtoGlobal.RoomMessage input) {
        return RealmAdditional.put(realm, input.getAdditionalData(), input.getAdditionalType());
    }

    public static RealmAdditional put(Realm realm, String additionalData, int additionalType) {
        RealmAdditional realmAdditional = realm.createObject(RealmAdditional.class, AppUtils.makeRandomId());
        realmAdditional.setAdditionalType(additionalType);
        realmAdditional.setAdditionalData(additionalData);

        return realmAdditional;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public int getAdditionalType() {
        return AdditionalType;
    }

    public void setAdditionalType(int additionalType) {
        AdditionalType = additionalType;
    }
}

package net.iGap.realm;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.iGap.module.AppUtils;
import net.iGap.module.structs.StructWebView;
import net.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.net_iGap_realm_RealmAdditionalRealmProxy;

@Parcel(implementations = {net_iGap_realm_RealmAdditionalRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmAdditional.class})
public class RealmAdditional extends RealmObject {

    @PrimaryKey
    private long id;
    private String additionalData;
    private int AdditionalType;


    public static RealmAdditional put(final ProtoGlobal.RoomMessage input) {
        Realm realm = Realm.getDefaultInstance();
        RealmAdditional realmAdditional = realm.createObject(RealmAdditional.class ,  AppUtils.makeRandomId());
        realmAdditional.setAdditionalType(input.getAdditionalType());
        realmAdditional.setAdditionalData(input.getAdditionalData());

        realm.close();
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

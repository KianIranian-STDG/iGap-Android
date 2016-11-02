package com.iGap.realm;

import io.realm.RealmObject;
import io.realm.RealmStringRealmProxy;
import org.parceler.Parcel;

@Parcel(implementations = { RealmStringRealmProxy.class },
    value = Parcel.Serialization.BEAN,
    analyze = { RealmString.class })
public class RealmString extends RealmObject {

    private String string;

    public String getString() {
        return string;
    }

    public void setString(String phone) {
        this.string = phone;
    }
}

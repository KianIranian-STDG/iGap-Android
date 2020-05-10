package net.iGap.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRecentChargeNumber extends RealmObject {
    @PrimaryKey
    private String number;

    public static void put(Realm realm, String number) {
        RealmRecentChargeNumber realmRecentChargeNumber = realm.where(RealmRecentChargeNumber.class).equalTo(RealmRecentChargeNumberFields.NUMBER, number).findFirst();

        if (realmRecentChargeNumber != null) {
            realm.executeTransactionAsync(asyncRealm -> asyncRealm.createObject(RealmRecentChargeNumber.class, number));
        }
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}

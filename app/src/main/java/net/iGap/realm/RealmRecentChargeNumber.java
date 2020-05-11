package net.iGap.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRecentChargeNumber extends RealmObject {
    @PrimaryKey
    private String number;
    private int type;

    public static void put(Realm realm, String number, int type) {
        realm.executeTransactionAsync(asyncRealm -> {
            RealmRecentChargeNumber realmRecentChargeNumber = realm.where(RealmRecentChargeNumber.class)
                    .equalTo(RealmRecentChargeNumberFields.NUMBER, number)
                    .equalTo(RealmRecentChargeNumberFields.TYPE, type).findFirst();

            if (realmRecentChargeNumber == null) {
                realmRecentChargeNumber = asyncRealm.createObject(RealmRecentChargeNumber.class, number);
            }

            realmRecentChargeNumber.setType(type);

        });

    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}

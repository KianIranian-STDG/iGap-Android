package net.iGap.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRecentChargeNumber extends RealmObject {
    private String number;
    private int type;

    public static void put(Realm asyncRealm, String number, int type) {
        RealmRecentChargeNumber realmRecentChargeNumber = asyncRealm.where(RealmRecentChargeNumber.class)
                .equalTo(RealmRecentChargeNumberFields.NUMBER, number)
                .equalTo(RealmRecentChargeNumberFields.TYPE, type).findFirst();

        if (realmRecentChargeNumber == null) {
            realmRecentChargeNumber = asyncRealm.createObject(RealmRecentChargeNumber.class);
            realmRecentChargeNumber.setNumber(number);
            realmRecentChargeNumber.setType(type);
        }
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

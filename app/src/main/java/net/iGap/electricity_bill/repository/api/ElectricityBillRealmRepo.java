package net.iGap.electricity_bill.repository.api;

import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileGetEmail;

import io.realm.Realm;

public class ElectricityBillRealmRepo {

    private Realm realm;
    private RealmUserInfo userInfo;

    public ElectricityBillRealmRepo() {
        updateUserInfo();
    }

    public String getUserNum() {
        return userInfo.getUserInfo().getPhoneNumber();
    }

    public String getUserEmail() {
        return userInfo.getEmail();
    }

    // Realm and Data

    private void updateUserInfo() {
        userInfo = getRealm().where(RealmUserInfo.class).findFirst();
        closeRealm();

        if (userInfo.getEmail() == null)
            new RequestUserProfileGetEmail().userProfileGetEmail();
    }

    private Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    private void closeRealm() {
        realm.close();
    }

}

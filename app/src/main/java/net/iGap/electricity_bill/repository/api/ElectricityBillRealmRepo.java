package net.iGap.electricity_bill.repository.api;

import net.iGap.DbManager;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileGetEmail;

import io.realm.Realm;

public class ElectricityBillRealmRepo {

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
        DbManager.getInstance().doRealmTask(realm -> {
            userInfo = realm.where(RealmUserInfo.class).findFirst();
        });

        if (userInfo.getEmail() == null)
            new RequestUserProfileGetEmail().userProfileGetEmail();
    }

}

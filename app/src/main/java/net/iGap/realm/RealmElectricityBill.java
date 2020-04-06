package net.iGap.realm;

import net.iGap.module.accountManager.DbManager;
import net.iGap.request.RequestUserProfileGetEmail;

public class RealmElectricityBill {

    private RealmUserInfo userInfo;

    public RealmElectricityBill() {
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

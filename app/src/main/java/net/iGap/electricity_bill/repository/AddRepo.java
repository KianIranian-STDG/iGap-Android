package net.iGap.electricity_bill.repository;

import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileGetEmail;

import io.realm.Realm;

public class AddRepo {

    private Realm realm;
    private RealmUserInfo userInfo;

    public AddRepo() {
        updateUserInfo();
    }

    public String getUserNum() {
        return userInfo.getUserInfo().getPhoneNumber();
    }

    public String getUserEmail() {
        return userInfo.getEmail();
    }

    // Realm and Data

    public void deleteAccount() {
        userInfo.deleteKuknos();
    }

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

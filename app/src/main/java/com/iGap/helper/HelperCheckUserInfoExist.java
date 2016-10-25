package com.iGap.helper;

import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.request.RequestUserInfo;

import io.realm.Realm;


public class HelperCheckUserInfoExist {

    /**
     * check user info exist in RealmRegisteredUserInfo , if exist return true and
     * if isn't exist send RequestUserInfo and get info for that user and finally
     * set info in RealmRegisteredUserInfo
     */

    public static boolean checkUserInfoExist(long userId) {

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
        if (realmRegisteredInfo != null) {
            return true;
        }

        new RequestUserInfo().userInfo(userId);

        realm.close();

        return false;
    }
}

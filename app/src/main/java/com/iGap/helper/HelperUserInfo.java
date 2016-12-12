package com.iGap.helper;

import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.request.RequestUserInfo;

import io.realm.Realm;

public class HelperUserInfo {

    /**
     * compare user cacheId , if was equal don't do anything
     * otherwise send request for get user info
     *
     * @param userId  userId for get old cacheId from RealmRegisteredInfo
     * @param cacheId new cacheId
     * @return return true if need update otherwise return false
     */

    public static boolean needUpdateUser(long userId, String cacheId) {

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();

        if (realmRegisteredInfo.getCacheId().equals(cacheId)) {
            return false;
        }
        new RequestUserInfo().userInfo(userId);

        realm.close();
        return true;
    }

}

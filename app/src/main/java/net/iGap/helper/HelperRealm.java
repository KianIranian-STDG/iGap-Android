/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import net.iGap.module.accountManager.DbManager;

import io.realm.RealmModel;

/**
 * helper methods while working with Realm
 * note: when any field of classes was changed, update this helper.
 */
public final class HelperRealm {
    private HelperRealm() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    /**
     * when call this method all objects in realm will be deleted
     */

    public static void realmTruncate() {
        DbManager.getInstance().doRealmTransaction(realm -> {
            realm.deleteAll();
        });
    }

    public static <E extends RealmModel> void copyOrUpdateToRealm(E object) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                realm.copyToRealmOrUpdate(object);
            });
        }).start();
    }
}

/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.response;

import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnInfo;
import net.iGap.proto.ProtoUserContactsGetBlockedList;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmRegisteredInfo;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class UserContactsGetBlockedListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;


    public UserContactsGetBlockedListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.Builder builder = (ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.Builder) message;
        List<ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.User> list = builder.getUserList();
        DbManager.getInstance().doRealmTask(realm -> {
            /**
             * reset blocked user in RealmRegisteredInfo and realm contact
             */

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<RealmRegisteredInfo> results = realm.where(RealmRegisteredInfo.class).equalTo("blockUser", true).findAll();
                    for (RealmRegisteredInfo item : results) {
                        item.setBlockUser(false);
                    }

                    RealmResults<RealmContacts> resultsContacts = realm.where(RealmContacts.class).equalTo("blockUser", true).findAll();
                    for (RealmContacts item : resultsContacts) {
                        item.setBlockUser(false);
                    }
                }
            });


            for (ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.User user : list) {
                RealmRegisteredInfo.getRegistrationInfo(user.getUserId(), user.getCacheId(), realm, new OnInfo() {
                    @Override
                    public void onInfo(Long registeredId) {
                        RealmRegisteredInfo.updateBlock(registeredId, true);
                        RealmContacts.updateBlock(registeredId, true);
                    }
                });
            }

        });
    }


    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}



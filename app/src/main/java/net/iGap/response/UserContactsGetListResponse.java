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

import net.iGap.controllers.MessageController;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.G;
import net.iGap.helper.HelperTimeOut;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoUserContactsGetList;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmRegisteredInfo;

public class UserContactsGetListResponse extends MessageHandler {

    private static long getListTime;
    public int actionId;
    public Object message;
    public String identity;

    public UserContactsGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoUserContactsGetList.UserContactsGetListResponse.Builder builder = (ProtoUserContactsGetList.UserContactsGetListResponse.Builder) message;

        /**
         * for avoid from multiple running in same time.
         * (( hint : we have an error for this class and now use from timeout.
         * in next version of app will be checked that any of users get this error again or no ))
         */
        if (HelperTimeOut.timeoutChecking(0, getListTime, 0)) {//Config.GET_CONTACT_LIST_TIME_OUT
            getListTime = System.currentTimeMillis();
            DbManager.getInstance().doRealmTransaction(realm -> {
                realm.delete(RealmContacts.class);

                for (ProtoGlobal.RegisteredUser registerUser : builder.getRegisteredUserList()) {
                    RealmRegisteredInfo.putOrUpdate(realm, registerUser);
                    RealmContacts.putOrUpdate(realm, registerUser);
                }
            });
            DbManager.getInstance().doRealmTransaction(realm -> {
                MessageController.getInstance(AccountManager.selectedAccount).getStories(realm.where(RealmContacts.class).findAll().size());
            });
            G.refreshRealmUi();
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (G.onContactsGetList != null) {
                        G.onContactsGetList.onContactsGetList();
                    }
                }
            });
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onContactsGetList != null) {
            G.onContactsGetList.onContactsGetListTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
    }
}



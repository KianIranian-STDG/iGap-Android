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

import net.iGap.G;
import net.iGap.controllers.MessageController;
import net.iGap.helper.HelperPreferences;
import net.iGap.module.Contacts;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserContactsImport;
import net.iGap.realm.RealmContacts;
import net.iGap.request.RequestUserContactImport;
import net.iGap.request.RequestUserContactsGetList;
import net.iGap.request.RequestUserInfo;

public class UserContactsImportResponse extends MessageHandler {

    private static final String TAG = "aabolfazlContact";
    public int actionId;
    public Object message;
    public Object identity;

    public UserContactsImportResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoUserContactsImport.UserContactsImportResponse.Builder builder = (ProtoUserContactsImport.UserContactsImportResponse.Builder) message;

        boolean getContactList = true;
        if (identity != null)
            if (identity instanceof Boolean) {
                getContactList = (Boolean) identity;
            } else if (identity instanceof String) {
                if (identity.equals(RequestUserContactImport.KEY)) {
                    new RequestUserInfo().contactImportWithCallBack(builder.getRegisteredContacts(0).getUserId());
                }
            }


        if (G.onQueueSendContact != null) {
            G.onQueueSendContact.sendContact();
        }
        else{
            Contacts.isSendingContactToServer = false;
        }


        if (getContactList) {
            G.serverHashContact = G.localHashContact;
            new RequestUserContactsGetList().userContactGetList();
        }

        DbManager.getInstance().doRealmTransaction(realm -> {
            MessageController.getInstance(AccountManager.selectedAccount).getStories(realm.where(RealmContacts.class).findAll().size());
        });
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();

        Contacts.isSendingContactToServer = false;
        G.onQueueSendContact = null;

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (majorCode == 118) {
            if (minorCode == 5) {
                HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.EXCEED_CONTACTS_CHUNK, true);
            }
            if (minorCode == 7) {
                HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.EXCEED_CONTACTS_NUMBER, true);
            }
        }

        /**
         * even the import wasn't successful send request for get contacts list
         */
        new RequestUserContactsGetList().userContactGetList();
    }
}



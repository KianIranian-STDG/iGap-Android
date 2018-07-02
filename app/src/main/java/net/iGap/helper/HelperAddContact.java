/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.helper;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import net.iGap.G;
import net.iGap.R;

import java.util.ArrayList;

public class HelperAddContact {

    public static void addContact(String displayName, String codeNumber, String phone) {

        String saveNumber;
        if (phone.startsWith("0")) {
            saveNumber = codeNumber + phone.substring(1, phone.length());
        } else {
            saveNumber = codeNumber + phone;
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        AccountManager accountManager = AccountManager.get(G.context);
        if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final Account account = new Account("" + saveNumber, G.context.getPackageName());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, account.type)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, account.name)
                .build());

//        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
//        builder.withValue(RawContacts.ACCOUNT_NAME, account.name);
//        builder.withValue(RawContacts.ACCOUNT_TYPE, account.type);
//        builder.withValue(RawContacts.SYNC1, username);
//        operationList.add(builder.build());

        //------------------------------------------------------ Names
        if (displayName != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName)
                    .build());
        }
        //------------------------------------------------------ Mobile Number
        if (phone != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, saveNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }


        ops.add(ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(
                        ContactsContract.Data.CONTENT_URI, true))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.net.iGap.profile")
                .withValue(ContactsContract.Data.DATA1, 12345)
                .withValue(ContactsContract.Data.DATA2, "Telegram Profile")
                .withValue(ContactsContract.Data.DATA3, "+" + saveNumber)
                .build());

        try {
            G.context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Uri addCallerIsSyncAdapterParameter(Uri uri, boolean isSyncOperation) {
        if (isSyncOperation) {
            return uri.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
        }
        return uri;
    }
}

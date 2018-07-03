/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import net.iGap.G;
import net.iGap.realm.RealmRegisteredInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static net.iGap.G.context;

public final class ContactUtils {
    private Context mContext;
    private Uri mContactUri;
    private String mContactID;

    public ContactUtils(Context context, Uri contactUri) {
        this.mContext = context;
        this.mContactUri = contactUri;

        // getting contact ID
        Cursor cursorID = mContext.getContentResolver().query(mContactUri, new String[]{ContactsContract.Contacts._ID}, null, null, null);

        if (cursorID != null && cursorID.moveToFirst()) {
            mContactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            cursorID.close();
        }
    }

    @Nullable
    public Uri getPhotoUri() {
        ContentResolver contentResolver = mContext.getContentResolver();

        try {
            Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "=" + mContactID + " AND "

                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null, null);

            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(mContactID));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public Bitmap retrievePhoto() {
        try {
            Bitmap photo = null;
            InputStream inputStream =
                    ContactsContract.Contacts.openContactPhotoInputStream(mContext.getContentResolver(), ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(mContactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }

            assert inputStream != null;
            inputStream.close();
            return photo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String retrieveNumber() {
        Cursor c = mContext.getContentResolver().query(mContactUri, null, null, null, null);
        String cNumber = null;
        if (c.moveToFirst()) {
            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (hasPhone.equalsIgnoreCase("1")) {
                cNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        }
        c.close();
        return cNumber;
    }

    public String retrieveName() {
        String contactName = null;

        // querying contact data store
        Cursor cursor = mContext.getContentResolver().query(mContactUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            cursor.close();
        }

        return contactName;
    }

    public static void addContactToPhoneBook(RealmRegisteredInfo contact) {

        String accountName = StartupActions.getiGapAccountInstance().name;
        String accountType = StartupActions.getiGapAccountInstance().type;

        ContentResolver contentResolver = context.getContentResolver();
        try {
            Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_NAME, accountName).appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_TYPE, accountType).build();
            contentResolver.delete(rawContactUri, ContactsContract.RawContacts.SYNC2 + " = " + contact.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<ContentProviderOperation> query = new ArrayList<>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, accountName);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, accountType);
        builder.withValue(ContactsContract.RawContacts.SYNC1, contact.getPhoneNumber());
        builder.withValue(ContactsContract.RawContacts.SYNC2, contact.getId());
        query.add(builder.build());

        query.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhoneNumber())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);

        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.getFirstName());
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contact.getLastName());
        query.add(builder.build());

        /*final String IM_LABEL = "iGap protocol";
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, 0);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM);
        contentValues.put(ContactsContract.CommonDataKinds.Im.LABEL, IM_LABEL);
        contentValues.put(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM);
        contentValues.put(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL, IM_LABEL);
        contentValues.put(ContactsContract.CommonDataKinds.Im.DATA, currentAccount.name);
        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValues(contentValues);
        query.add(builder.build());*/

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.net.iGap.profile");
        builder.withValue(ContactsContract.Data.DATA1, contact.getId());
        builder.withValue(ContactsContract.Data.DATA2, "iGap Profile");
        builder.withValue(ContactsContract.Data.DATA3, "+" + contact.getPhoneNumber());
        builder.withValue(ContactsContract.Data.DATA4, contact.getId());
        query.add(builder.build());
        try {
            G.context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

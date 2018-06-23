/*******************************************************************************
 * Copyright 2010 Sam Steele 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.iGap.helper.contact;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Entity;
import android.util.Log;

import net.iGap.G;
import net.iGap.R;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author sam
 */
public class ContactsSyncAdapterService extends Service {
    private static final String TAG = "ContactsSyncAdapter";
    private static SyncAdapterImpl sSyncAdapter = null;
    private static ContentResolver mContentResolver = null;
    private static String UsernameColumn = RawContacts.SYNC1;
    private static String PhotoTimestampColumn = RawContacts.SYNC2;
    private static Realm realm;
    private static RealmResults<RealmContacts> results;

    public ContactsSyncAdapterService() {
        super();
    }

    private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        private Context mContext;

        public SyncAdapterImpl(Context context) {
            super(context, true);
            mContext = context;
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            Log.i("CCCCCCCCCC", "1 results.size(): ");
            results = getRealm().where(RealmContacts.class).findAll();
            Log.i("CCCCCCCCCC", "results.size(): " + results.size());
            for (RealmContacts item : results) {
                updateContactPhoneByName(item.getFirst_name(), item.getLast_name());
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        ret = getSyncAdapter().getSyncAdapterBinder();
        return ret;
    }

    private SyncAdapterImpl getSyncAdapter() {
        if (sSyncAdapter == null)
            sSyncAdapter = new SyncAdapterImpl(this);
        return sSyncAdapter;
    }


    private static void updateContactStatus(ArrayList<ContentProviderOperation> operationList, long rawContactId, String status) {
        Uri rawContactUri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId);
        Uri entityUri = Uri.withAppendedPath(rawContactUri, Entity.CONTENT_DIRECTORY);
        Cursor c = mContentResolver.query(entityUri, new String[]{RawContacts.SOURCE_ID, Entity.DATA_ID, Entity.MIMETYPE, Entity.DATA1}, null, null, null);
        try {
            if (c != null) {
                while (c.moveToNext()) {
                    if (!c.isNull(1)) {
                        String mimeType = c.getString(2);

                        if (mimeType.equals("vnd.android.cursor.item/vnd.net.iGap.profile")) {
                            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.StatusUpdates.CONTENT_URI);
                            builder.withValue(ContactsContract.StatusUpdates.DATA_ID, c.getLong(1));
                            builder.withValue(ContactsContract.StatusUpdates.STATUS, status);
                            builder.withValue(ContactsContract.StatusUpdates.STATUS_RES_PACKAGE, "vnd.android.cursor.item/vnd.net.iGap.profile");
                            builder.withValue(ContactsContract.StatusUpdates.STATUS_LABEL, R.string.app_name);
                            builder.withValue(ContactsContract.StatusUpdates.STATUS_ICON, R.mipmap.icon);
                            builder.withValue(ContactsContract.StatusUpdates.STATUS_TIMESTAMP, System.currentTimeMillis());
                            operationList.add(builder.build());
                        }
                    }
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }


    private static class SyncEntry {
        public Long raw_id = 0L;
        public Long photo_timestamp = null;
    }

    private void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {


    }


    private static long getRawContactIdByName(String givenName, String familyName) throws Exception {
        ContentResolver contentResolver = G.context.getContentResolver();

        // Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

        // Create query column array.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};

        // Create where condition clause.
        String displayName = givenName + " " + familyName;
        String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + displayName + "'";

        // Query raw contact id through RawContacts uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Return the query cursor.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);

        long rawContactId = -1;

        if (cursor != null) {
            // Get contact count that has same display name, generally it should be one.
            int queryResultCount = cursor.getCount();
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if (queryResultCount > 0) {
                // Move to the first row in the result cursor.
                cursor.moveToFirst();
                // Get raw_contact_id.
                rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            }
        }

        return rawContactId;
    }


    /*
     * Update contact phone number by contact name.
     * Return update contact number, commonly there should has one contact be updated.
     */
    private static int updateContactPhoneByName(String givenName, String familyName) {
        int ret = 0;

        ContentResolver contentResolver = G.context.getContentResolver();

        // Get raw contact id by display name.
        long rawContactId = 0;
        try {
            rawContactId = getRawContactIdByName(givenName, familyName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("CCCCCCCCCC", "updateContactPhoneByName: " + rawContactId);

        // Update data table phone number use contact raw contact id.
        if (rawContactId > -1) {


            ret = 1;
        } else {
            ret = 0;
        }

        return ret;
    }


    /* Update phone number with raw contact id and phone type.*/
    private void updatePhoneNumber(ContentResolver contentResolver, long rawContactId, int phoneType, String newPhoneNumber) {
        // Create content values object.
        ContentValues contentValues = new ContentValues();

        // Put new phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber);

        // Create query condition, query with the raw contact id.
        StringBuffer whereClauseBuf = new StringBuffer();

        // Specify the update contact id.
        whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        whereClauseBuf.append("=");
        whereClauseBuf.append(rawContactId);

        // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
        whereClauseBuf.append(" and ");
        whereClauseBuf.append(ContactsContract.Data.MIMETYPE);
        whereClauseBuf.append(" = '");
        String mimetype = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
        whereClauseBuf.append(mimetype);
        whereClauseBuf.append("'");

        // Specify phone type.
        whereClauseBuf.append(" and ");
        whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.TYPE);
        whereClauseBuf.append(" = ");
        whereClauseBuf.append(phoneType);

        // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
        Uri dataUri = ContactsContract.Data.CONTENT_URI;

        // Get update data count.
        int updateCount = contentResolver.update(dataUri, contentValues, whereClauseBuf.toString(), null);
    }

    private static Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }

        return realm;
    }
}

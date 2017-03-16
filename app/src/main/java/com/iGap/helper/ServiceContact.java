package com.iGap.helper;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import com.iGap.Config;
import com.iGap.G;
import com.iGap.module.StructListOfContact;
import com.iGap.request.RequestUserContactImport;
import java.util.ArrayList;

import static com.iGap.G.context;

public class ServiceContact extends Service {

    private MyContentObserver contentObserver;
    private long fetchContactTime;

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        contentObserver = new MyContentObserver();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override public void run() {
                getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contentObserver);
            }
        }, 10000);
        return Service.START_STICKY;
    }

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver() {
            super(null);
        }

        @Override public void onChange(boolean selfChange) {

            final int permissionReadContact = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
            if ((permissionReadContact == PackageManager.PERMISSION_GRANTED)) {
                /**
                 * for avoid from run multiple this code at the same time
                 * because sometimes onChange was run multiple times
                 */
                if (HelperTimeOut.timeoutChecking(0, fetchContactTime, Config.FETCH_CONTACT_TIME_OUT)) {
                    fetchContactTime = System.currentTimeMillis();
                    fetchContacts();
                }
            }
        }

        private void fetchContacts() {
            G.handler.post(new Runnable() {
                @Override public void run() {
                    try {
                        ArrayList<StructListOfContact> contactList = new ArrayList<>();
                        ContentResolver cr = G.context.getContentResolver();
                        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                        if (cur != null) {
                            if (cur.getCount() > 0) {
                                while (cur.moveToNext()) {
                                    StructListOfContact itemContact = new StructListOfContact();
                                    itemContact.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] {
                                            id
                                        }, null);
                                        if (pCur != null) {
                                            while (pCur.moveToNext()) {
                                                int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                                if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                                    itemContact.setPhone(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                                                }
                                            }
                                            pCur.close();
                                        }
                                    }
                                    contactList.add(itemContact);
                                }
                            }
                            cur.close();
                        }
                        ArrayList<StructListOfContact> resultContactList = new ArrayList<>();
                        for (int i = 0; i < contactList.size(); i++) {

                            if (contactList.get(i).getPhone() != null) {
                                StructListOfContact itemContact = new StructListOfContact();
                                if (contactList.get(i) != null && contactList.get(i).getDisplayName() != null) {
                                    String[] sp = contactList.get(i).getDisplayName().split(" ");
                                    if (sp.length == 1) {

                                        itemContact.setFirstName(sp[0]);
                                        itemContact.setLastName("");
                                        itemContact.setPhone(contactList.get(i).getPhone());
                                        itemContact.setDisplayName(contactList.get(i).displayName);
                                    } else if (sp.length == 2) {
                                        itemContact.setFirstName(sp[0]);
                                        itemContact.setLastName(sp[1]);
                                        itemContact.setPhone(contactList.get(i).getPhone());
                                        itemContact.setDisplayName(contactList.get(i).displayName);
                                    } else if (sp.length == 3) {
                                        itemContact.setFirstName(sp[0]);
                                        itemContact.setLastName(sp[1] + sp[2]);
                                        itemContact.setPhone(contactList.get(i).getPhone());
                                        itemContact.setDisplayName(contactList.get(i).displayName);
                                    } else if (sp.length >= 3) {
                                        itemContact.setFirstName(contactList.get(i).getDisplayName());
                                        itemContact.setLastName("");
                                        itemContact.setPhone(contactList.get(i).getPhone());
                                        itemContact.setDisplayName(contactList.get(i).displayName);
                                    }
                                    resultContactList.add(itemContact);
                                }
                            }
                        }
                        RequestUserContactImport listContact = new RequestUserContactImport();
                        listContact.contactImport(resultContactList, false);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

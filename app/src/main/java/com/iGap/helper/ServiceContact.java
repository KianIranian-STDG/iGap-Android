package com.iGap.helper;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import com.iGap.G;
import com.iGap.module.StructListOfContact;
import com.iGap.request.RequestUserContactImport;
import java.util.ArrayList;

public class ServiceContact extends Service {

    private MyContentObserver contentObserver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MMM", "ServiceContact onStartCommand");
        contentObserver = new MyContentObserver();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contentObserver);
            }
        }, 10000);
        return Service.START_STICKY;
    }

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {

            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    ArrayList<StructListOfContact> contactList = new ArrayList<>();
                    ContentResolver cr = G.context.getContentResolver();
                    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                    assert cur != null;
                    if (cur.getCount() > 0) {
                        while (cur.moveToNext()) {
                            StructListOfContact itemContact = new StructListOfContact();
                            itemContact.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{
                                        id
                                }, null);
                                assert pCur != null;
                                while (pCur.moveToNext()) {
                                    int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                        itemContact.setPhone(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                                    }
                                }
                                pCur.close();
                            }
                            contactList.add(itemContact);
                        }
                    }
                    cur.close();
                    ArrayList<StructListOfContact> resultContactList = new ArrayList<>();
                    for (int i = 0; i < contactList.size(); i++) {

                        if (contactList.get(i).getPhone() != null) {
                            StructListOfContact itemContact = new StructListOfContact();
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
                    RequestUserContactImport listContact = new RequestUserContactImport();
                    listContact.contactImport(resultContactList, false);
                }
            });
        }
    }
}

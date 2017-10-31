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

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;

/**
 * work with saved contacts in database
 */
public class Contacts {

    public static boolean getContact = true;
    private static final int PHONE_CONTACT_FETCH_LIMIT = 30;
    public static int phoneContactId = 0;


    /**
     * retrieve contacts from database
     *
     * @param filter filter contacts
     * @return List<StructContactInfo>
     */
    public static List<StructContactInfo> retrieve(String filter) {
        ArrayList<StructContactInfo> items = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmContacts> contacts;
        if (filter == null) {
            contacts = realm.where(RealmContacts.class).findAllSorted(RealmContactsFields.DISPLAY_NAME);
        } else {
            contacts = realm.where(RealmContacts.class).contains(RealmContactsFields.DISPLAY_NAME, filter).findAllSorted(RealmContactsFields.DISPLAY_NAME);
        }

        String lastHeader = "";
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i) == null) {
                continue;
            }
            String header = contacts.get(i).getDisplay_name();
            long peerId = contacts.get(i).getId();
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, contacts.get(i).getId());

            // new header exists
            if (lastHeader.isEmpty() || (!lastHeader.isEmpty() && !header.isEmpty() && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {
                StructContactInfo structContactInfo = new StructContactInfo(peerId, header, "", true, false, "");
                structContactInfo.initials = contacts.get(i).getInitials();
                structContactInfo.color = contacts.get(i).getColor();
                structContactInfo.avatar = realmRegisteredInfo.getLastAvatar();
                items.add(structContactInfo);
            } else {
                StructContactInfo structContactInfo = new StructContactInfo(peerId, header, "", false, false, "");
                structContactInfo.initials = contacts.get(i).getInitials();
                structContactInfo.color = contacts.get(i).getColor();
                structContactInfo.avatar = realmRegisteredInfo.getLastAvatar();
                items.add(structContactInfo);
            }
            lastHeader = header;
        }

        realm.close();
        return items;
    }

    public static ArrayList<StructListOfContact> getListOfContact() { //get List Of Contact
        ArrayList<StructListOfContact> contactList = new ArrayList<>();
        ContentResolver cr = G.context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur != null) {
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    int contactId = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)}, null);

                        if (pCur != null) {
                            while (pCur.moveToNext()) {
                                StructListOfContact itemContact = new StructListOfContact();
                                itemContact.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                                itemContact.setPhone(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                                contactList.add(itemContact);
                            }
                            pCur.close();
                        }
                    }
                }
            }
            cur.close();
        }

        ArrayList<StructListOfContact> resultContactList = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {

            if (contactList.get(i).getPhone() != null && contactList.get(i).getDisplayName() != null) {
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

        return resultContactList;
    }

    public static void getMobileListContact() { //get List Of Contact

        int fetchCount = 0;
        boolean isEnd = false;

        ArrayList<String> tempList = new ArrayList<>();
        ArrayList<StructListOfContact> contactList = new ArrayList<>();
        ContentResolver cr = G.context.getContentResolver();

        String startContactId = ">=" + phoneContactId;
        String selection = ContactsContract.Contacts._ID + startContactId;

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, selection, null, null);//ContactsContract.Contacts.DISPLAY_NAME + " ASC"

        if (cur != null) {
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    if (!getContact) {
                        return;
                    }

                    int contactId = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    phoneContactId = contactId + 1;//plus for fetch next contact in future query
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)}, null);
                        if (pCur != null) {
                            while (pCur.moveToNext()) {
                                String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                if (!tempList.contains(number.replace("[\\s\\-()]", "").replace(" ", ""))) {
                                    StructListOfContact itemContact = new StructListOfContact();
                                    itemContact.setDisplayName(name);
                                    itemContact.setPhone(number);
                                    contactList.add(itemContact);
                                    tempList.add(number.replace("[\\s\\-()]", "").replace(" ", ""));
                                }
                            }
                            pCur.close();
                        }
                    }

                    fetchCount++;

                    if (fetchCount > PHONE_CONTACT_FETCH_LIMIT) {
                        break;
                    }

                }
            }
            cur.close();
        }

        if (fetchCount < PHONE_CONTACT_FETCH_LIMIT) {
            isEnd = true;
        }


        if (G.onPhoneContact != null) {
            G.onPhoneContact.onPhoneContact(contactList, isEnd);
        }

        if (!isEnd) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new LongOperation().execute();
                }
            }, 500);
        }
    }

    private static class LongOperation extends AsyncTask<Void, Void, ArrayList<StructListOfContact>> {
        @Override
        protected ArrayList<StructListOfContact> doInBackground(Void... params) {
            Contacts.getMobileListContact();
            return null;
        }
    }
}

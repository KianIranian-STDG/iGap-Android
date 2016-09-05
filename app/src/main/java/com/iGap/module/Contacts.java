package com.iGap.module;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.iGap.G;
import com.iGap.realm.RealmContacts;
import com.iGap.request.RequestUserContactImport;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * work with saved contacts in database
 */
public class Contacts {

    /**
     * retrieve contacts from database
     *
     * @param filter filter contacts
     * @return List<StructContactInfo>
     */
    public static List<StructContactInfo> retrieve(String filter) {
        ArrayList<StructContactInfo> items = new ArrayList<>();

        RealmResults<RealmContacts> contacts;
        Realm realm = Realm.getDefaultInstance();
        if (filter == null) {
            contacts = realm.where(RealmContacts.class).findAll();
        } else {
            contacts = realm
                    .where(RealmContacts.class)
                    .contains("display_name", filter)
                    .findAll();
        }
        realm.close();

        String lastHeader = "";
        for (int i = 0; i < contacts.size(); i++) {
            String header = contacts.get(i).getDisplay_name();
            long peerId = contacts.get(i).getId();

            // new header exists
            if (lastHeader.isEmpty() || (!lastHeader.isEmpty() && !header.isEmpty() && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {
                // TODO: 9/5/2016 [Alireza Eskandarpour Shoferi] implement contact last seen
                items.add(new StructContactInfo(peerId, header, "", true));
            } else {
                items.add(new StructContactInfo(peerId, header, "", false));
            }
            lastHeader = header;
        }

        return items;
    }

    public static ArrayList<StructListOfContact> getListOfContact() { //get List Of Contact

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
                            id}, null);
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
        listContact.contactImport(resultContactList);

        return resultContactList;
    }
}

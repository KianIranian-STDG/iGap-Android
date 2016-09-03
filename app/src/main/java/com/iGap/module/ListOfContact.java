package com.iGap.module;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.iGap.G;
import com.iGap.adapter.ContactNamesAdapter;
import com.iGap.realm.RealmContacts;
import com.iGap.request.RequestUserContactImport;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class ListOfContact {

    public static ArrayList<ContactNamesAdapter.LineItem> Retrive(String s) {


        ArrayList<ContactNamesAdapter.LineItem> mItems = new ArrayList<>();
        RealmResults<RealmContacts> items = null;

        G.realm = Realm.getInstance(G.realmConfig);

        if (s.equals("")) {
            items = G.realm.where(RealmContacts.class).findAll();
        } else {
            items = G.realm
                    .where(RealmContacts.class)
                    .contains("display_name", s)
                    .findAll();
        }

        //Insert headers into list of items.
        String lastHeader = "";
        int sectionManager = -1;
        int headerCount = 0;
        int sectionFirstPosition = 0;

        for (int i = 0; i < items.size(); i++) {
            try {
                String header = items.get(i).getDisplay_name();

                if (!TextUtils.equals(lastHeader.toLowerCase(), header.toLowerCase())) {
                    // Insert new header view and update section data.
                    sectionManager = (sectionManager + 1) % 2;
                    sectionFirstPosition = i + headerCount;
                    lastHeader = header.toUpperCase();
                    headerCount += 1;
                    mItems.add(new ContactNamesAdapter.LineItem(items.get(i).getId(), header.toUpperCase(), "", true, sectionManager, sectionFirstPosition));
                }
                mItems.add(new ContactNamesAdapter.LineItem(items.get(i).getId(), items.get(i).getDisplay_name(), "Last seen recently", false, sectionManager, sectionFirstPosition));

            } catch (Exception e) {
            }
        }

        return mItems;
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

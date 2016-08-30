package com.iGap.module;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.iGap.G;
import com.iGap.adapter.ContactNamesAdapter;
import com.iGap.request.RequestUserContactImport;

import java.util.ArrayList;

/**
 * Created by Rahmani on 8/29/2016.
 */
public class ListOfContact {

//    StructListOfContact itemContact = new StructListOfContact();

    public static ArrayList<ContactNamesAdapter.LineItem> Retrive(String s) {
        Cursor cur = null;
        ArrayList<ContactNamesAdapter.LineItem> mItems = new ArrayList<>();
        try {
            ContentResolver cr = G.context.getContentResolver();
            cur = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},
                    null, null, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //String  names = null;
        String[] items = new String[cur.getCount()];

        if (cur.getCount() > 0) {
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);

                // String id = cur.getString(
                //       cur.getColumnIndex(ContactsContract.Contacts._ID));

                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phone = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));

                try {
                    if (!name.toLowerCase().toString().trim().equals("null") && name.toLowerCase().toString().trim().contains(s.toLowerCase()))
                        items[i] = name;

                    //items[i] = "Dominika Faniz";

                } catch (Exception e) {
                }
            }
        }
        //Insert headers into list of items.

        String lastHeader = "";
        int sectionManager = -1;
        int headerCount = 0;
        int sectionFirstPosition = 0;

        for (int i = 0; i < items.length; i++) {
            try {
                String header = items[i].substring(0, 1);

                if (!TextUtils.equals(lastHeader.toLowerCase(), header.toLowerCase())) {
                    // Insert new header view and update section data.
                    sectionManager = (sectionManager + 1) % 2;
                    sectionFirstPosition = i + headerCount;
                    lastHeader = header.toUpperCase();
                    headerCount += 1;
                    mItems.add(new ContactNamesAdapter.LineItem(header.toUpperCase(), "", true, sectionManager, sectionFirstPosition));
                }
                mItems.add(new ContactNamesAdapter.LineItem(items[i], "Last seen recently", false, sectionManager, sectionFirstPosition));

            } catch (Exception e) {
            }
        }
        return mItems;
    }

    public static void getListOfContact() {

        ArrayList<StructListOfContact> itemList = new ArrayList<>();


        String whereName = ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
        Cursor nameCur = G.context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, null, null, null);
        while (nameCur.moveToNext()) {
            StructListOfContact item = new StructListOfContact();
//            String display = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));// get family and name togater
            item.setFirst_name(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
            item.setLast_name(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
            item.setPhone(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

            itemList.add(item);
        }
        nameCur.close();

        RequestUserContactImport t = new RequestUserContactImport();
        t.contactImport(itemList);

        Log.i("TAGTAGTTT", "Phone : ");

    }

//    public static void getListOfContact() {
//        ArrayList<StructListOfContact>itemList = new ArrayList<>();
//        ContentResolver cr = G.context.getContentResolver();
//        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//        if (cur.getCount() > 0) {
//            while (cur.moveToNext()) {
//                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
//                if (Integer.parseInt(cur.getString(
//                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                    Cursor pCur = cr.query(
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                            new String[]{id}, null);
//                    while (pCur.moveToNext()) {
//                        StructListOfContact item  = new StructListOfContact();
//                        item.setPhone(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//                        item.setFirst_name(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
////                        item.setLast_name( cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
//                        itemList.add(item);
//
//                    }
//                    pCur.close();
//                }
//            }
//        }
//        RequestUserContactImport t = new RequestUserContactImport();
//        t.contactImport(itemList);
//
//    }

}

/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.G;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperString;
import net.iGap.module.Contacts;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.observers.interfaces.OnQueueSendContact;
import net.iGap.request.RequestUserContactImport;
import net.iGap.request.RequestUserContactsGetList;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmPhoneContacts extends RealmObject {

    private static int PHONE_CONTACT_FETCH_LIMIT = 100;
    @PrimaryKey
    private String phone;
    private String firstName;
    private String lastName;

    private static List<StructListOfContact> tmpList;
    private static int counter = 1;

    public static void sendContactList(final List<StructListOfContact> list, final boolean force, final boolean getContactList) {

        if (Contacts.isSendingContactToServer) {
            return;
        }
        final List<StructListOfContact> _list = fillContactsToDB(list);
        if (_list.size() > 0) {
            if (_list.size() > PHONE_CONTACT_FETCH_LIMIT) {
                RequestUserContactImport listContact = new RequestUserContactImport();
                final int loopCount = _list.size() / PHONE_CONTACT_FETCH_LIMIT;
                tmpList = _list.subList(0,PHONE_CONTACT_FETCH_LIMIT);
                Contacts.isSendingContactToServer = true;
                addListToDB(tmpList);
                listContact.contactImport(tmpList, force, false);
                G.onQueueSendContact = new OnQueueSendContact() {
                    @Override
                    public void sendContact() {
                        counter++;
                        tmpList.clear();
                        if (counter <= loopCount){
                            RequestUserContactImport listContact = new RequestUserContactImport();
                            tmpList = _list.subList(0,PHONE_CONTACT_FETCH_LIMIT);
                            addListToDB(tmpList);
                            listContact.contactImport(tmpList, force, false);
                        }
                        else{
                            if(_list.size() % PHONE_CONTACT_FETCH_LIMIT == 0){
                                G.onQueueSendContact = null;
                                Contacts.isSendingContactToServer = false;
                                return;
                            }
                            else{
                                RequestUserContactImport listContact = new RequestUserContactImport();
                                tmpList = _list.subList(0 , _list.size() - 1 );
                                addListToDB(tmpList);
                                listContact.contactImport(tmpList, force, true);
                                G.onQueueSendContact = null;
                            }
                        }
                    }
                };
            }
            else {
                RequestUserContactImport listContact = new RequestUserContactImport();
                addListToDB(_list);
                listContact.contactImport(_list, force, true);
                G.onQueueSendContact = new OnQueueSendContact() {
                    @Override
                    public void sendContact() {
                        G.onQueueSendContact = null;
                        Contacts.isSendingContactToServer = false;
                    }
                };
            }
        } else if (getContactList) {
            new RequestUserContactsGetList().userContactGetList();
        }
    }

    private static void addListToDB(final List<StructListOfContact> list) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            for (int i = 0; i < list.size(); i++) {
                addContactToDB(list.get(i), realm);
            }
        });
    }

    private static void addContactToDB(final StructListOfContact item, Realm realm) {
        try {
            RealmPhoneContacts realmPhoneContacts = new RealmPhoneContacts();
            realmPhoneContacts.setPhone(checkString(item));
//            realmPhoneContacts.setFirstName(item.firstName);
//            realmPhoneContacts.setLastName(item.lastName);
            realm.copyToRealmOrUpdate(realmPhoneContacts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<StructListOfContact> fillContactsToDB(final List<StructListOfContact> list) {

        final List<StructListOfContact> notImportedList = new ArrayList<>();

        if (list == null) {
            return notImportedList;
        }

        DbManager.getInstance().doRealmTransaction(realm -> {
            for (int i = 0; i < list.size(); i++) {
                StructListOfContact _item = list.get(i);
                if (_item == null || _item.getPhone() == null || _item.getPhone().length() == 0) {
                    continue;
                }

                try {
                    if (realm.where(RealmPhoneContacts.class).equalTo("phone", checkString(_item)).findFirst() == null) {
                        notImportedList.add(_item);
                    }
                } catch (IllegalArgumentException e) {
                    HelperLog.getInstance().setErrorLog(e);
                }

            }
        });

        return notImportedList;
    }

    private static String checkString(StructListOfContact item) {
        String phoneText = item.getPhone() + "_" + item.firstName + item.lastName;

        phoneText.getBytes(Charset.forName("UTF-8"));
        return phoneText;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {

        try {
            this.firstName = firstName;
        } catch (Exception e) {
            this.firstName = HelperString.getUtf8String(firstName);
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {

        try {
            this.lastName = lastName;
        } catch (Exception e) {
            this.lastName = HelperString.getUtf8String(lastName);
        }
    }
}

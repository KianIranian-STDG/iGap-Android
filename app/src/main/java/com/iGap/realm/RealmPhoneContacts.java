package com.iGap.realm;

import com.iGap.module.StructListOfContact;
import com.iGap.request.RequestUserContactImport;
import com.iGap.request.RequestUserContactsGetList;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.util.ArrayList;

/**
 * Created by android3 on 4/19/2017.
 */

public class RealmPhoneContacts extends RealmObject {

    @PrimaryKey private String phone;
    private String firstName;
    private String lastName;

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
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static void sendContactList(final ArrayList<StructListOfContact> list, final boolean force) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<StructListOfContact> _list = fillContactsToDB(list);
                if (_list.size() > 0) {
                    RequestUserContactImport listContact = new RequestUserContactImport();
                    listContact.contactImport(_list, force);
                } else {
                    new RequestUserContactsGetList().userContactGetList();
                }
            }
        }).start();
    }

    private static void addContactToDB(final StructListOfContact item, Realm realm) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmPhoneContacts _realmPhoneContacts = realm.createObject(RealmPhoneContacts.class, item.getPhone());
                _realmPhoneContacts.setFirstName(item.firstName);
                _realmPhoneContacts.setLastName(item.lastName);
            }
        });
    }

    private static ArrayList<StructListOfContact> fillContactsToDB(ArrayList<StructListOfContact> list) {

        ArrayList<StructListOfContact> notImportedList = new ArrayList<>();

        if (list == null) {
            return notImportedList;
        }

        Realm realm = Realm.getDefaultInstance();

        for (int i = 0; i < list.size(); i++) {

            boolean _addItem = false;
            final StructListOfContact _item = list.get(i);
            final RealmPhoneContacts _realmPhoneContacts = realm.where(RealmPhoneContacts.class).equalTo(RealmPhoneContactsFields.PHONE, _item.getPhone()).findFirst();

            if (_realmPhoneContacts == null) {
                _addItem = true;

                addContactToDB(_item, realm);
            } else {
                if (!_item.getFirstName().equals(_realmPhoneContacts.getFirstName()) || !_item.getLastName().equals(_realmPhoneContacts.getLastName())) {
                    _addItem = true;

                    // if one number save with tow or more different name
                    int count = 0;
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).getPhone().equals(_item.getPhone())) {
                            count++;
                            if (count > 1) {
                                _addItem = false;
                                break;
                            }
                        }
                    }

                    if (_addItem) {
                        if (_realmPhoneContacts != null) {

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    _realmPhoneContacts.setFirstName(_item.getFirstName());
                                    _realmPhoneContacts.setLastName(_item.getLastName());
                                }
                            });
                        }
                    }
                }
            }

            if (_addItem) {
                notImportedList.add(_item);
            }
        }

        realm.close();

        return notImportedList;
    }
}

package com.iGap.request;

import com.iGap.proto.ProtoUserContactsEdit;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;

import io.realm.Realm;

public class RequestUserContactsEdit {

    public void contactsEdit(long phone, String first_name, String last_name) {
        Realm realm = Realm.getDefaultInstance();
        ProtoUserContactsEdit.UserContactsEdit.Builder builder = ProtoUserContactsEdit.UserContactsEdit.newBuilder();
        RealmContacts realmItem = realm.where(RealmContacts.class).equalTo(RealmContactsFields.PHONE, phone).findFirst();

        if (realmItem != null) {

            builder.setPhone(realmItem.getPhone());
            builder.setFirstName(realmItem.getFirst_name());
            builder.setLastName(realmItem.getLast_name());
        }

        RequestWrapper requestWrapper = new RequestWrapper(109, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        realm.close();
    }

}
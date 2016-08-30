package com.iGap.request;

import com.iGap.G;
import com.iGap.proto.ProtoUserContactsEdit;
import com.iGap.realm.RealmUserContactsGetListResponse;

public class RequestUserContactsEdit {

    public void contactsEdit(long phone, String first_name, String last_name) {

        ProtoUserContactsEdit.UserContactsEdit.Builder builder = ProtoUserContactsEdit.UserContactsEdit.newBuilder();
        RealmUserContactsGetListResponse realmItem = G.realm.where(RealmUserContactsGetListResponse.class).equalTo("phone", phone).findFirst();

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
    }

}
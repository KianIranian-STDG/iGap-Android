package com.iGap.request;

import android.util.Log;

import com.iGap.module.StructListOfContact;
import com.iGap.proto.ProtoUserContactsImport;

import java.util.ArrayList;

public class RequestUserContactImport {

    public void contactImport(ArrayList<StructListOfContact> itemContactList, boolean force) {

        RequestWrapper requestWrapper = new RequestWrapper(106, contact(itemContactList, force));
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void contactImportAndGetReponse(ArrayList<StructListOfContact> itemContactList, boolean force) {

        RequestWrapper requestWrapper = new RequestWrapper(106, contact(itemContactList, force), "identity");
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private ProtoUserContactsImport.UserContactsImport.Builder contact(ArrayList<StructListOfContact> itemContactList, boolean force) {
        ProtoUserContactsImport.UserContactsImport.Builder userContactsImport = ProtoUserContactsImport.UserContactsImport.newBuilder();

        for (int i = 0; i < itemContactList.size(); i++) {
            String phone = itemContactList.get(i).getPhone();
            String first_name = itemContactList.get(i).getFirstName();
            String last_name = itemContactList.get(i).getLastName();
            Log.i("OOO", "contactImport first_name : " + first_name);
            Log.i("OOO", "contactImport phone : " + phone);

            ProtoUserContactsImport.UserContactsImport.Contact.Builder contact = ProtoUserContactsImport.UserContactsImport.Contact.newBuilder();

            contact.setPhone(phone);
            contact.setFirstName(first_name);
            contact.setLastName(last_name);

            userContactsImport.setForce(force);
            userContactsImport.addContacts(i, contact);
        }

        return userContactsImport;
    }
}
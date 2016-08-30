package com.iGap.request;

import android.util.Log;

import com.iGap.module.StructListOfContact;
import com.iGap.proto.ProtoUserContactsImport;

import java.util.ArrayList;

public class RequestUserContactImport {

    public void contactImport(ArrayList<StructListOfContact> itemContactList) {

        ProtoUserContactsImport.UserContactsImport.Builder userContactsImport = ProtoUserContactsImport.UserContactsImport.newBuilder();

        for (int i = 0; i < itemContactList.size(); i++) {

            String phone = itemContactList.get(i).getPhone();
            String first_name = itemContactList.get(i).getFirst_name();
            String last_name = itemContactList.get(i).getLast_name();

            ProtoUserContactsImport.UserContactsImport.Contact.Builder contact = ProtoUserContactsImport.UserContactsImport.Contact.newBuilder();
            if (phone != null) {
                contact.setPhone(phone);
            } else {
                contact.setPhone("a");
            }

            if (first_name != null) {
                contact.setFirstName(first_name);
            } else {
                contact.setFirstName("a");
            }
            if (last_name != null) {
                contact.setLastName(last_name);

            } else {
                contact.setLastName("a");
            }

            Log.i("TAGTAGTTT", "Name : " + contact.getPhone());
            Log.i("TAGTAGTTT", "family : " + contact.getFirstName());
            Log.i("TAGTAGTTT", "Phone : " + contact.getLastName());

            //userContactsImport.setContacts(i+1,contact);
            userContactsImport.addContacts(i, contact);

            Log.i("TAGTAGTTT", "Phone : " + last_name);

        }

        RequestWrapper requestWrapper = new RequestWrapper(106, userContactsImport);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
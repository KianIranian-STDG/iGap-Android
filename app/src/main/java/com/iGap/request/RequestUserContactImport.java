package com.iGap.request;

import com.iGap.module.StructListOfContact;
import com.iGap.proto.ProtoUserContactsImport;
import java.util.ArrayList;

public class RequestUserContactImport {

    public void contactImport(ArrayList<StructListOfContact> itemContactList) {

        ProtoUserContactsImport.UserContactsImport.Builder userContactsImport =
            ProtoUserContactsImport.UserContactsImport.newBuilder();

        for (int i = 0; i < itemContactList.size(); i++) {

            String phone = itemContactList.get(i).getPhone();
            String first_name = itemContactList.get(i).getFirstName();
            String last_name = itemContactList.get(i).getLastName();

            ProtoUserContactsImport.UserContactsImport.Contact.Builder contact =
                ProtoUserContactsImport.UserContactsImport.Contact.newBuilder();

            contact.setPhone(phone);
            contact.setFirstName(first_name);
            contact.setLastName(last_name);

            userContactsImport.addContacts(i, contact);
        }

        RequestWrapper requestWrapper = new RequestWrapper(106, userContactsImport);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
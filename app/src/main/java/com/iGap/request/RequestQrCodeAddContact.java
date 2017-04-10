package com.iGap.request;

import com.iGap.proto.ProtoQrCodeAddContact;

public class RequestQrCodeAddContact {

    public void qrCodeAddContact(String phone, String firstName, String lastName) {

        ProtoQrCodeAddContact.QrCodeAddContact.Builder builder = ProtoQrCodeAddContact.QrCodeAddContact.newBuilder();
        builder.setPhone(phone);
        builder.setFirstName(firstName);
        builder.setLastName(lastName);

        RequestWrapper requestWrapper = new RequestWrapper(803, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

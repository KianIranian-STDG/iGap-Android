// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.iGap.interface_package.OnComplete;


/**
 * get sms from igap sms center for register user in program
 */

public class IncomingSms extends BroadcastReceiver {

    private String PhoneNumberServer;
    private OnComplete listener;

    public IncomingSms() {
        super();
    }

    public IncomingSms(String PhoneNumberServer, OnComplete listener) {
        this.PhoneNumberServer = PhoneNumberServer;
        this.listener = listener;
    }

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {

            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    if (senderNum.contains(PhoneNumberServer)) {
                        listener.onComplete(true, message);
                        break;

                    }
                }
            }
        } catch (Exception e) {

        }
    }
}

// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.iGap.G;
import com.iGap.interfaces.OnSmsReceive;


/**
 * get sms from igap sms center for register user in program
 */

public class IncomingSms extends BroadcastReceiver {

    private OnSmsReceive listener;

    public IncomingSms() {
        super();
    }

    public IncomingSms(OnSmsReceive listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("SSS", "Receive SMS");
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i("SSS", "1 phoneNumber : " + phoneNumber + "  ||  message : " + message);
                    for (Long number : G.smsNumbers) {
                        Log.i("SSS", "Server Number : " + number);
                        if (phoneNumber.contains(number.toString())) {
                            Log.i("SSS", "onSmsReceive start");
                            listener.onSmsReceive(message);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

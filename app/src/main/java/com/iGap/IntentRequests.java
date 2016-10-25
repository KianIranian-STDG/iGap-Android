package com.iGap;

import android.support.annotation.IntDef;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/25/2016.
 */

@IntDef({IntentRequests.REQ_CROP, IntentRequests.REQ_CAMERA, IntentRequests.REQ_GALLERY})
public @interface IntentRequests {
    int REQ_CROP = 100;
    int REQ_CAMERA = 101;
    int REQ_GALLERY = 102;
}

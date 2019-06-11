/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.iGap.G;
import net.iGap.module.SHP_SETTING;

public class HelperTracker {

    public static final String TRACKER_INSTALL_USER = "TRACKER_INSTALL_USER";
    public static final String TRACKER_SUBMIT_NUMBER = "TRACKER_SUBMIT_NUMBER";
    public static final String TRACKER_ACTIVATION_CODE = "TRACKER_ACTIVATION_CODE";
    public static final String TRACKER_REGISTRATION_USER = "TRACKER_REGISTRATION_USER";
    public static final String TRACKER_REGISTRATION_NEW_USER = "TRACKER_REGISTRATION_NEW_USER";
    public static final String TRACKER_ROOM_PAGE = "TRACKER_ROOM_PAGE";
    public static final String TRACKER_DISCOVERY_PAGE = "TRACKER_DISCOVERY_PAGE";
    public static final String TRACKER_CALL_PAGE = "TRACKER_CALL_PAGE";
    public static final String TRACKER_CHAT_VIEW = "TRACKER_CHAT_VIEW";
    public static final String TRACKER_GROUP_VIEW = "TRACKER_GROUP_VIEW";
    public static final String TRACKER_CHANNEL_VIEW = "TRACKER_CHANNEL_VIEW";
    public static final String TRACKER_BOT_VIEW = "TRACKER_BOT_VIEW";
    public static final String TRACKER_WALLET_PAGE = "TRACKER_WALLET_PAGE";
    public static final String TRACKER_NEARBY_PAGE = "TRACKER_NEARBY_PAGE";
    public static final String TRACKER_CHANGE_LANGUAGE = "TRACKER_CHANGE_LANGUAGE";
    public static final String TRACKER_CREATE_CHANNEL = "TRACKER_CREATE_CHANNEL";
    public static final String TRACKER_CREATE_GROUP = "TRACKER_CREATE_GROUP";
    public static final String TRACKER_VOICE_CALL_CONNECTING = "TRACKER_VOICE_CALL_CONNECTING";
    public static final String TRACKER_VOICE_CALL_CONNECTED = "TRACKER_VOICE_CALL_CONNECTED";
    public static final String TRACKER_VIDEO_CALL_CONNECTING = "TRACKER_VIDEO_CALL_CONNECTING";
    public static final String TRACKER_VIDEO_CALL_CONNECTED = "TRACKER_VIDEO_CALL_CONNECTED";
    public static final String TRACKER_FINANCIAL_SERVICES = "TRACKER_FINANCIAL_SERVICES";

    public static void sendTracker(String trackerTag) {

        boolean allowSendTracker = true;

        if (trackerTag.equals(TRACKER_INSTALL_USER) && HelperPreferences.getInstance().readBoolean(SHP_SETTING.KEY_TRACKER_FILE, SHP_SETTING.KEY_TRACKER_INSTALL_USER)) {
            allowSendTracker = false;
        }

        if (allowSendTracker) {
            FirebaseAnalytics.getInstance(G.context).logEvent(trackerTag, null);
        }
    }
}

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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.enums.CallState;
import net.iGap.proto.ProtoSignalingOffer;

import ir.metrix.Metrix;


public class HelperTracker {

    private static Tracker mTracker;

    private static final String CATEGORY_SETTING = "Setting@";
    private static final String CATEGORY_COMMUNICATION = "Communication@";
    private static final String CATEGORY_REGISTRATION = "Registration@";
    private static final String CATEGORY_DISCOVERY = "Discovery@";
    private static final String CATEGORY_ACCOUNT = "Account@";
    private static final String CATEGORY_BILL = "Bill@";
    private static final String CATEGORY_MOMENTS = "Moments@";

    public static final String TRACKER_CHANGE_LANGUAGE = CATEGORY_SETTING + "TRACKER_CHANGE_LANGUAGE";

    public static final String TRACKER_CALL_PAGE = CATEGORY_COMMUNICATION + "TRACKER_CALL_PAGE";
    public static final String TRACKER_VOICE_CALL_CONNECTING = CATEGORY_COMMUNICATION + "TRACKER_VOICE_CALL_CONNECTING";
    public static final String TRACKER_VOICE_CALL_CONNECTED = CATEGORY_COMMUNICATION + "TRACKER_VOICE_CALL_CONNECTED";
    public static final String TRACKER_VIDEO_CALL_CONNECTING = CATEGORY_COMMUNICATION + "TRACKER_VIDEO_CALL_CONNECTING";
    public static final String TRACKER_VIDEO_CALL_CONNECTED = CATEGORY_COMMUNICATION + "TRACKER_VIDEO_CALL_CONNECTED";
    public static final String TRACKER_CHAT_VIEW = CATEGORY_COMMUNICATION + "TRACKER_CHAT_VIEW";
    public static final String TRACKER_GROUP_VIEW = CATEGORY_COMMUNICATION + "TRACKER_GROUP_VIEW";
    public static final String TRACKER_CHANNEL_VIEW = CATEGORY_COMMUNICATION + "TRACKER_CHANNEL_VIEW";
    public static final String TRACKER_BOT_VIEW = CATEGORY_COMMUNICATION + "TRACKER_BOT_VIEW";
    public static final String TRACKER_ROOM_PAGE = CATEGORY_COMMUNICATION + "TRACKER_ROOM_PAGE";
    public static final String TRACKER_CREATE_CHANNEL = CATEGORY_COMMUNICATION + "TRACKER_CREATE_CHANNEL";
    public static final String TRACKER_CREATE_GROUP = CATEGORY_COMMUNICATION + "TRACKER_CREATE_GROUP";
    public static final String TRACKER_INVITE_FRIEND = CATEGORY_COMMUNICATION + "TRACKER_INVITE_FRIEND";

    public static final String TRACKER_INSTALL_USER = CATEGORY_REGISTRATION + "TRACKER_INSTALL_USER";
    public static final String TRACKER_SUBMIT_NUMBER = CATEGORY_REGISTRATION + "TRACKER_SUBMIT_NUMBER";
    public static final String TRACKER_ACTIVATION_CODE = CATEGORY_REGISTRATION + "TRACKER_ACTIVATION_CODE";
    public static final String TRACKER_QR_REGISTRATION = CATEGORY_REGISTRATION + "TRACKER_QR_REGISTRATION";
    public static final String TRACKER_REGISTRATION_USER = CATEGORY_REGISTRATION + "TRACKER_REGISTRATION_USER";
    public static final String TRACKER_REGISTRATION_NEW_USER = CATEGORY_REGISTRATION + "TRACKER_REGISTRATION_NEW_USER";
    public static final String TRACKER_TWO_STEP = CATEGORY_REGISTRATION + "TRACKER_TWO_STEP";
    public static final String TRACKER_ENTRY_PHONE = CATEGORY_REGISTRATION + "TRACKER_ENTRY_PHONE";
    public static final String TRACKER_ENTRY_NEW_USER_INFO = CATEGORY_REGISTRATION + "TRACKER_ENTRY_NEW_USER_INFO";
    public static final String TRACKER_CHANGE_LANGUAGE_FIRST = CATEGORY_REGISTRATION + "TRACKER_CHANGE_LANGUAGE_FIRST";

    public static final String TRACKER_DISCOVERY_PAGE = CATEGORY_DISCOVERY + "TRACKER_DISCOVERY_PAGE";
    public static final String TRACKER_WALLET_PAGE = CATEGORY_DISCOVERY + "TRACKER_WALLET_PAGE";
    public static final String TRACKER_NEARBY_PAGE = CATEGORY_DISCOVERY + "TRACKER_NEARBY_PAGE";
    public static final String TRACKER_FINANCIAL_SERVICES = CATEGORY_DISCOVERY + "TRACKER_FINANCIAL_SERVICES";


    public static final String TRACKER_LOGOUT_ACCOUNT = CATEGORY_ACCOUNT + "TRACKER_LOGOUT_ACCOUNT";
    public static final String TRACKER_DELETE_ACCOUNT = CATEGORY_ACCOUNT + "TRACKER_DELETE_ACCOUNT";
    public static final String TRACKER_ADD_NEW_ACCOUNT = CATEGORY_ACCOUNT + "TRACKER_ADD_NEW_ACCOUNT";


    public static final String TRACKER_BILL_PAGE = CATEGORY_BILL + "TRACKER_BILL_PAGE";
    public static final String TRACKER_PHONE_BILL_PAY = CATEGORY_BILL + "TRACKER_PHONE_BILL_PAY";
    public static final String TRACKER_ELECTRIC_BILL_PAY = CATEGORY_BILL + "TRACKER_ELECTRIC_BILL_PAY";
    public static final String TRACKER_ADD_BILL_PAGE = CATEGORY_BILL + "TRACKER_ADD_BILL_PAGE";
    public static final String TRACKER_ADD_BILL_TO_LIST = CATEGORY_BILL + "TRACKER_ADD_BILL_TO_LIST";
    public static final String TRACKER_SERVICE_BILL_PAGE = CATEGORY_BILL + "TRACKER_SERVICE_BILL_PAGE";
    public static final String TRACKER_SERVICE_BILL_PAY = CATEGORY_BILL + "TRACKER_SERVICE_BILL_PAY";
    public static final String TRACKER_FINE_BILL_PAGE = CATEGORY_BILL + "TRACKER_FINE_BILL_PAGE";
    public static final String TRACKER_FINE_BILL_PAY = CATEGORY_BILL + "TRACKER_FINE_BILL_PAY";
    public static final String TRACKER_MOBILE_BILL_PAY = CATEGORY_BILL + "TRACKER_MOBILE_BILL_PAY";
    public static final String TRACKER_GAS_BILL_PAY = CATEGORY_BILL + "TRACKER_GAS_BILL_PAY";


    public static final String TRACKER_MOMENTS_TAB = CATEGORY_MOMENTS + "TRACKER_MOMENTS_TAB";
    public static final String TRACKER_MOMENTS_CREATE_PICTURE_PAGE = CATEGORY_MOMENTS + "TRACKER_MOMENTS_CRATE_PICTURE_PAGE";
    public static final String TRACKER_MOMENTS_CREATE_TEXT_PAGE = CATEGORY_MOMENTS + "TRACKER_MOMENTS_CRATE_TEXT_PAGE";
    public static final String TRACKER_MOMENTS_SUBMIT_PICTURE_PAGE = CATEGORY_MOMENTS + "TRACKER_MOMENTS_SUBMIT_PICTURE_PAGE";
    public static final String TRACKER_MOMENTS_SUBMIT_PICTURE_TEXT = CATEGORY_MOMENTS + "TRACKER_MOMENTS_SUBMIT_PICTURE_TEXT";
    public static final String TRACKER_MOMENTS_SHOW = CATEGORY_MOMENTS + "TRACKER_MOMENTS_SHOW";
    public static final String TRACKER_MOMENTS_MY_PAGE = CATEGORY_MOMENTS + "TRACKER_MOMENTS_MY_PAGE";


    private static HelperTracker instance;
    private static Boolean canSendMetrixEvent;

    public static Boolean getCanSendMetrixEvent() {
        if (canSendMetrixEvent == null) {
            canSendMetrixEvent = false;
            String packageName = G.context.getPackageName();
            canSendMetrixEvent = packageName != null && packageName.toLowerCase().equals("net.igap");
        }
        return canSendMetrixEvent;
    }

    public static HelperTracker getInstance() {
        if (instance == null) {
            instance = new HelperTracker();
        }
        return instance;
    }

    synchronized private static Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(G.context);
            mTracker = analytics.newTracker(R.xml.global_track);
        }
        return mTracker;
    }

    public static void sendTracker(String trackerTag) {
        if (BuildConfig.DEBUG) {
            return;
        }

        boolean allowSendTracker = true;

        if (trackerTag.equals(TRACKER_INSTALL_USER) && HelperPreferences.getInstance().readBoolean(SHP_SETTING.KEY_TRACKER_FILE, SHP_SETTING.KEY_TRACKER_INSTALL_USER)) {
            allowSendTracker = false;
        } else {
            HelperPreferences.getInstance().putBoolean(SHP_SETTING.KEY_TRACKER_FILE, SHP_SETTING.KEY_TRACKER_INSTALL_USER, true);
        }

        if (allowSendTracker && getCanSendMetrixEvent()) {
            switch (trackerTag) {
                case TRACKER_CHANGE_LANGUAGE:
                    Metrix.newEvent("rvwun");
                    //CHANGE_LANGUAGE_UNIQUE
                    Metrix.newEvent("hrvwa");
                    break;
                case TRACKER_CALL_PAGE:
                    Metrix.newEvent("mlrxn");
                    //CALL_PAGE_UNIQUE
                    Metrix.newEvent("nrnyz");
                    break;
                case TRACKER_VOICE_CALL_CONNECTING:
                    Metrix.newEvent("qsjti");
                    //VOICE_CALL_CONNECTING_UNIQUE
                    Metrix.newEvent("zkinz");
                    break;
                case TRACKER_VOICE_CALL_CONNECTED:
                    Metrix.newEvent("znfwd");
                    //VOICE_CALL_CONNECTED_UNIQUE
                    Metrix.newEvent("cqyft");
                    break;
                case TRACKER_VIDEO_CALL_CONNECTING:
                    Metrix.newEvent("sxkav");
                    //VIDEO_CALL_CONNECTING_UNIQUE
                    Metrix.newEvent("mwiez");
                    break;
                case TRACKER_VIDEO_CALL_CONNECTED:
                    Metrix.newEvent("dcsqk");
                    //VIDEO_CALL_CONNECTED_UNIQUE
                    Metrix.newEvent("qpetc");
                    break;
                case TRACKER_CHAT_VIEW:
                    Metrix.newEvent("rszqm");
                    //CHAT_VIEW_UNIQUE
                    Metrix.newEvent("njcym");
                    break;
                case TRACKER_GROUP_VIEW:
                    Metrix.newEvent("htwef");
                    //GROUP_VIEW_UNIQUE
                    Metrix.newEvent("exgqd");
                    break;
                case TRACKER_CHANNEL_VIEW:
                    Metrix.newEvent("smkkz");
                    //CHANNEL_VIEW_UNIQUE
                    Metrix.newEvent("rnroy");
                    break;
                case TRACKER_BOT_VIEW:
                    Metrix.newEvent("sgozq");
                    //BOT_VIEW_UNIQUE
                    Metrix.newEvent("fjouv");
                    break;
                case TRACKER_ROOM_PAGE:
                    Metrix.newEvent("hnahq");
                    //ROOM_PAGE_UNIQUE
                    Metrix.newEvent("pvtkd");
                    break;
                case TRACKER_CREATE_CHANNEL:
                    Metrix.newEvent("hzodo");
                    //CREATE_CHANNEL_UNIQUE
                    Metrix.newEvent("xdtkp");
                    break;
                case TRACKER_CREATE_GROUP:
                    Metrix.newEvent("szlrq");
                    //CREATE_GROUP_UNIQUE
                    Metrix.newEvent("sofvw");
                    break;
                case TRACKER_INVITE_FRIEND:
                    Metrix.newEvent("kvjqi");
                    //INVITE_FRIEND_UNIQUE
                    Metrix.newEvent("oggbs");
                    break;
                case TRACKER_INSTALL_USER:
                    Metrix.newEvent("zwhkn");
                    //INSTALL_USER_UNIQUE
                    Metrix.newEvent("qibti");
                    break;
                case TRACKER_SUBMIT_NUMBER:
                    Metrix.newEvent("hvxtt");
                    //SUBMIT_NUMBER_UNIQUE
                    Metrix.newEvent("ioouf");
                    break;
                case TRACKER_ACTIVATION_CODE:
                    Metrix.newEvent("jjrro");
                    //ACTIVATION_CODE_UNIQUE
                    Metrix.newEvent("qgwir");
                    break;
                case TRACKER_QR_REGISTRATION:
                    Metrix.newEvent("uufge");
                    //QR_REGISTRATION_UNIQUE
                    Metrix.newEvent("zujim");
                    break;
                case TRACKER_REGISTRATION_USER:
                    Metrix.newEvent("ooarp");
                    //REGISTRATION_USER_UNIQUE
                    Metrix.newEvent("wohlv");
                    break;
                case TRACKER_REGISTRATION_NEW_USER:
                    Metrix.newEvent("wthwa");

                    //REGISTRATION_UNIQUE
                    Metrix.newEvent("npmol");
                    break;
                case TRACKER_DISCOVERY_PAGE:
                    Metrix.newEvent("qkslv");
                    //DISCOVERY_PAGE_UNIQUE
                    Metrix.newEvent("bbkde");
                    break;
                case TRACKER_WALLET_PAGE:
                    Metrix.newEvent("yxhgb");
                    //WALLET_PAGE_UNIQUE
                    Metrix.newEvent("ecufj");
                    break;
                case TRACKER_NEARBY_PAGE:
                    Metrix.newEvent("vvcid");
                    //NEARBY_PAGE_UNIQUE
                    Metrix.newEvent("mkenc");
                    break;
                case TRACKER_FINANCIAL_SERVICES:
                    Metrix.newEvent("dbbfk");
                    //FINANCIAL_SERVICES_UNIQUE
                    Metrix.newEvent("iluis");
                    break;
                case TRACKER_BILL_PAGE:
                    Metrix.newEvent("lqduh");
                    //BILL_PAGE_UNIQUE
                    Metrix.newEvent("qftpu");
                    break;
                case TRACKER_PHONE_BILL_PAY:
                    Metrix.newEvent("rruab");
                    //PHONE_BILL_PAY_UNIQUE
                    Metrix.newEvent("wdasd");
                    break;
                case TRACKER_ELECTRIC_BILL_PAY:
                    Metrix.newEvent("fanwy");
                    //ELECTRIC_BILL_PAY_UNIQUE
                    Metrix.newEvent("bcavh");
                    break;
                case TRACKER_ADD_BILL_PAGE:
                    Metrix.newEvent("esqvs");
                    //ADD_BILL_PAGE_UNIQUE
                    Metrix.newEvent("axtjg");
                    break;
                case TRACKER_ADD_BILL_TO_LIST:
                    Metrix.newEvent("xhiip");
                    //ADD_BILL_TO_LIST_UNIQUE
                    Metrix.newEvent("fjowx");
                    break;
                case TRACKER_SERVICE_BILL_PAGE:
                    Metrix.newEvent("mdmnf");
                    //SERVICE_BILL_PAGE_UNIQUE
                    Metrix.newEvent("emtkx");
                    break;
                case TRACKER_SERVICE_BILL_PAY:
                    Metrix.newEvent("foxxx");
                    //SERVICE_BILL_PAY_UNIQUE
                    Metrix.newEvent("lrmuk");
                    break;
                case TRACKER_FINE_BILL_PAGE:
                    Metrix.newEvent("bkxjc");
                    //FINE_BILL_PAGE_UNIQUE
                    Metrix.newEvent("htfxo");
                    break;
                case TRACKER_FINE_BILL_PAY:
                    Metrix.newEvent("ufgna");
                    //FINE_BILL_PAY_UNIQUE
                    Metrix.newEvent("onybr");
                    break;
                case TRACKER_LOGOUT_ACCOUNT:
                    Metrix.newEvent("hzaqe");
                    //LOGOUT_ACCOUNT_UNIQUE
                    Metrix.newEvent("bxrrx");
                    break;
                case TRACKER_DELETE_ACCOUNT:
                    Metrix.newEvent("zbpyd");
                    //DELETE_ACCOUNT_UNIQUE
                    Metrix.newEvent("kwjrb");
                    break;
                case TRACKER_ADD_NEW_ACCOUNT:
                    Metrix.newEvent("bsxvt");
                    //ADD_NEW_ACCOUNT_UNIQUE
                    Metrix.newEvent("hnirh");
                    break;
                case TRACKER_TWO_STEP:
                    Metrix.newEvent("flrkw");
                    //TWO_STEP_UNIQUE
                    Metrix.newEvent("qwqpx");
                    break;
                case TRACKER_ENTRY_PHONE:
                    Metrix.newEvent("wnxfo");
                    //ENTRY_PHONE_UNIQUE
                    Metrix.newEvent("nfrct");
                    break;
                case TRACKER_MOBILE_BILL_PAY:
                    Metrix.newEvent("xnplh");
                    //MOBILE_BILL_PAY_UNIQUE
                    Metrix.newEvent("gdouh");
                    break;
                case TRACKER_GAS_BILL_PAY:
                    Metrix.newEvent("zdaaq");
                    //GAS_BILL_PAY_UNIQUE
                    Metrix.newEvent("wegcl");
                    break;
                case TRACKER_ENTRY_NEW_USER_INFO:
                    Metrix.newEvent("xsqei");
                    //ENTRY_NEW_USER_INFO_UNIQUE
                    Metrix.newEvent("gmtgx");
                    break;
                case TRACKER_CHANGE_LANGUAGE_FIRST:
                    Metrix.newEvent("lmkey");
                    //CHANGE_LANGUAGE_FIRST_UNIQUE
                    Metrix.newEvent("gxkzj");
                    break;
                case TRACKER_MOMENTS_TAB:
                    Metrix.newEvent("wxnsi");
                    //CHANGE_LANGUAGE_FIRST_UNIQUE
                    Metrix.newEvent("fcwfh");
                    break;
                case TRACKER_MOMENTS_CREATE_PICTURE_PAGE:
                    Metrix.newEvent("hxogy");
                    //CHANGE_LANGUAGE_FIRST_UNIQUE
                    Metrix.newEvent("rwupn");
                    break;
                case TRACKER_MOMENTS_CREATE_TEXT_PAGE:
                    Metrix.newEvent("apbev");
                    //CHANGE_LANGUAGE_FIRST_UNIQUE
                    Metrix.newEvent("nyaox");
                    break;
                case TRACKER_MOMENTS_SUBMIT_PICTURE_PAGE:
                    Metrix.newEvent("jedyu");
                    //CHANGE_LANGUAGE_FIRST_UNIQUE
                    Metrix.newEvent("rmtdz");
                    break;
                case TRACKER_MOMENTS_SUBMIT_PICTURE_TEXT:
                    Metrix.newEvent("nyvgw");
                    //CHANGE_LANGUAGE_FIRST_UNIQUE
                    Metrix.newEvent("enryv");
                    break;
                case TRACKER_MOMENTS_SHOW:
                    Metrix.newEvent("vvmsz");
                    //CHANGE_LANGUAGE_FIRST_UNIQUE
                    Metrix.newEvent("ufoeg");
                    break;
                case TRACKER_MOMENTS_MY_PAGE:
                    Metrix.newEvent("lpedc");
                    //CHANGE_LANGUAGE_FIRST_UNIQUE
                    Metrix.newEvent("zupls");
                    break;
            }

            String[] trackerType = trackerTag.split("@");
            String category = trackerType[0];
            String action = trackerType[1];

            FirebaseAnalytics.getInstance(G.context).logEvent(action, null);

            Tracker tracker = getDefaultTracker();
            tracker.send(new HitBuilders.EventBuilder(category, action).build());

            tracker.setScreenName(action);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public void sendCallEvent(ProtoSignalingOffer.SignalingOffer.Type callType, CallState callState) {
        if (callType == null || callState == null) {
            return;
        }

        if (callType.equals(ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING)) {
            if (callState.equals(CallState.CONNECTED)) {
                sendTracker(TRACKER_VIDEO_CALL_CONNECTED);
            } else if (callState.equals(CallState.CONNECTING)) {
                sendTracker(TRACKER_VIDEO_CALL_CONNECTING);
            }
        } else if (callType.equals(ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING)) {
            if (callState.equals(CallState.CONNECTED)) {
                sendTracker(TRACKER_VOICE_CALL_CONNECTED);
            } else if (callState.equals(CallState.CONNECTING)) {
                sendTracker(TRACKER_VOICE_CALL_CONNECTING);
            }
        }
    }
}

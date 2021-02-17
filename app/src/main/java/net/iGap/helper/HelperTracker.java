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

import com.caspian.otpsdk.context.ApplicationContext;
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

import ir.metrix.sdk.Metrix;
import ir.metrix.sdk.MetrixConfig;

public class HelperTracker {

    private static Tracker mTracker;

    private static final String CATEGORY_SETTING = "Setting@";
    private static final String CATEGORY_COMMUNICATION = "Communication@";
    private static final String CATEGORY_REGISTRATION = "Registration@";
    private static final String CATEGORY_DISCOVERY = "Discovery@";
    private static final String CATEGORY_ACCOUNT = "Account@";
    private static final String CATEGORY_BILL = "Bill@";

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


    private static HelperTracker instance;
    private static boolean canSendMetrixEvent = false;

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

        if (allowSendTracker && canSendMetrixEvent) {
            switch (trackerTag) {
                case TRACKER_CHANGE_LANGUAGE:
                    Metrix.getInstance().newEvent("rvwun");
                    //CHANGE_LANGUAGE_UNIQUE
                    Metrix.getInstance().newEvent("hrvwa");
                    break;
                case TRACKER_CALL_PAGE:
                    Metrix.getInstance().newEvent("mlrxn");
                    //CALL_PAGE_UNIQUE
                    Metrix.getInstance().newEvent("nrnyz");
                    break;
                case TRACKER_VOICE_CALL_CONNECTING:
                    Metrix.getInstance().newEvent("qsjti");
                    //VOICE_CALL_CONNECTING_UNIQUE
                    Metrix.getInstance().newEvent("zkinz");
                    break;
                case TRACKER_VOICE_CALL_CONNECTED:
                    Metrix.getInstance().newEvent("znfwd");
                    //VOICE_CALL_CONNECTED_UNIQUE
                    Metrix.getInstance().newEvent("cqyft");
                    break;
                case TRACKER_VIDEO_CALL_CONNECTING:
                    Metrix.getInstance().newEvent("sxkav");
                    //VIDEO_CALL_CONNECTING_UNIQUE
                    Metrix.getInstance().newEvent("mwiez");
                    break;
                case TRACKER_VIDEO_CALL_CONNECTED:
                    Metrix.getInstance().newEvent("dcsqk");
                    //VIDEO_CALL_CONNECTED_UNIQUE
                    Metrix.getInstance().newEvent("qpetc");
                    break;
                case TRACKER_CHAT_VIEW:
                    Metrix.getInstance().newEvent("rszqm");
                    //CHAT_VIEW_UNIQUE
                    Metrix.getInstance().newEvent("njcym");
                    break;
                case TRACKER_GROUP_VIEW:
                    Metrix.getInstance().newEvent("htwef");
                    //GROUP_VIEW_UNIQUE
                    Metrix.getInstance().newEvent("exgqd");
                    break;
                case TRACKER_CHANNEL_VIEW:
                    Metrix.getInstance().newEvent("smkkz");
                    //CHANNEL_VIEW_UNIQUE
                    Metrix.getInstance().newEvent("rnroy");
                    break;
                case TRACKER_BOT_VIEW:
                    Metrix.getInstance().newEvent("sgozq");
                    //BOT_VIEW_UNIQUE
                    Metrix.getInstance().newEvent("fjouv");
                    break;
                case TRACKER_ROOM_PAGE:
                    Metrix.getInstance().newEvent("hnahq");
                    //ROOM_PAGE_UNIQUE
                    Metrix.getInstance().newEvent("pvtkd");
                    break;
                case TRACKER_CREATE_CHANNEL:
                    Metrix.getInstance().newEvent("hzodo");
                    //CREATE_CHANNEL_UNIQUE
                    Metrix.getInstance().newEvent("xdtkp");
                    break;
                case TRACKER_CREATE_GROUP:
                    Metrix.getInstance().newEvent("szlrq");
                    //CREATE_GROUP_UNIQUE
                    Metrix.getInstance().newEvent("sofvw");
                    break;
                case TRACKER_INVITE_FRIEND:
                    Metrix.getInstance().newEvent("kvjqi");
                    //INVITE_FRIEND_UNIQUE
                    Metrix.getInstance().newEvent("oggbs");
                    break;
                case TRACKER_INSTALL_USER:
                    Metrix.getInstance().newEvent("zwhkn");
                    //INSTALL_USER_UNIQUE
                    Metrix.getInstance().newEvent("qibti");
                    break;
                case TRACKER_SUBMIT_NUMBER:
                    Metrix.getInstance().newEvent("hvxtt");
                    //SUBMIT_NUMBER_UNIQUE
                    Metrix.getInstance().newEvent("ioouf");
                    break;
                case TRACKER_ACTIVATION_CODE:
                    Metrix.getInstance().newEvent("jjrro");
                    //ACTIVATION_CODE_UNIQUE
                    Metrix.getInstance().newEvent("qgwir");
                    break;
                case TRACKER_QR_REGISTRATION:
                    Metrix.getInstance().newEvent("uufge");
                    //QR_REGISTRATION_UNIQUE
                    Metrix.getInstance().newEvent("zujim");
                    break;
                case TRACKER_REGISTRATION_USER:
                    Metrix.getInstance().newEvent("ooarp");
                    //REGISTRATION_USER_UNIQUE
                    Metrix.getInstance().newEvent("wohlv");
                    break;
                case TRACKER_REGISTRATION_NEW_USER:
                    Metrix.getInstance().newEvent("wthwa");

                    //REGISTRATION_UNIQUE
                    Metrix.getInstance().newEvent("npmol");
                    break;
                case TRACKER_DISCOVERY_PAGE:
                    Metrix.getInstance().newEvent("qkslv");
                    //DISCOVERY_PAGE_UNIQUE
                    Metrix.getInstance().newEvent("bbkde");
                    break;
                case TRACKER_WALLET_PAGE:
                    Metrix.getInstance().newEvent("yxhgb");
                    //WALLET_PAGE_UNIQUE
                    Metrix.getInstance().newEvent("ecufj");
                    break;
                case TRACKER_NEARBY_PAGE:
                    Metrix.getInstance().newEvent("vvcid");
                    //NEARBY_PAGE_UNIQUE
                    Metrix.getInstance().newEvent("mkenc");
                    break;
                case TRACKER_FINANCIAL_SERVICES:
                    Metrix.getInstance().newEvent("dbbfk");
                    //FINANCIAL_SERVICES_UNIQUE
                    Metrix.getInstance().newEvent("iluis");
                    break;
                case TRACKER_BILL_PAGE:
                    Metrix.getInstance().newEvent("lqduh");
                    //BILL_PAGE_UNIQUE
                    Metrix.getInstance().newEvent("qftpu");
                    break;
                case TRACKER_PHONE_BILL_PAY:
                    Metrix.getInstance().newEvent("rruab");
                    //PHONE_BILL_PAY_UNIQUE
                    Metrix.getInstance().newEvent("wdasd");
                    break;
                case TRACKER_ELECTRIC_BILL_PAY:
                    Metrix.getInstance().newEvent("fanwy");
                    //ELECTRIC_BILL_PAY_UNIQUE
                    Metrix.getInstance().newEvent("bcavh");
                    break;
                case TRACKER_ADD_BILL_PAGE:
                    Metrix.getInstance().newEvent("esqvs");
                    //ADD_BILL_PAGE_UNIQUE
                    Metrix.getInstance().newEvent("axtjg");
                    break;
                case TRACKER_ADD_BILL_TO_LIST:
                    Metrix.getInstance().newEvent("xhiip");
                    //ADD_BILL_TO_LIST_UNIQUE
                    Metrix.getInstance().newEvent("fjowx");
                    break;
                case TRACKER_SERVICE_BILL_PAGE:
                    Metrix.getInstance().newEvent("mdmnf");
                    //SERVICE_BILL_PAGE_UNIQUE
                    Metrix.getInstance().newEvent("emtkx");
                    break;
                case TRACKER_SERVICE_BILL_PAY:
                    Metrix.getInstance().newEvent("foxxx");
                    //SERVICE_BILL_PAY_UNIQUE
                    Metrix.getInstance().newEvent("lrmuk");
                    break;
                case TRACKER_FINE_BILL_PAGE:
                    Metrix.getInstance().newEvent("bkxjc");
                    //FINE_BILL_PAGE_UNIQUE
                    Metrix.getInstance().newEvent("htfxo");
                    break;
                case TRACKER_FINE_BILL_PAY:
                    Metrix.getInstance().newEvent("ufgna");
                    //FINE_BILL_PAY_UNIQUE
                    Metrix.getInstance().newEvent("onybr");
                    break;
                case TRACKER_LOGOUT_ACCOUNT:
                    Metrix.getInstance().newEvent("hzaqe");
                    //LOGOUT_ACCOUNT_UNIQUE
                    Metrix.getInstance().newEvent("bxrrx");
                    break;
                case TRACKER_DELETE_ACCOUNT:
                    Metrix.getInstance().newEvent("zbpyd");
                    //DELETE_ACCOUNT_UNIQUE
                    Metrix.getInstance().newEvent("kwjrb");
                    break;
                case TRACKER_ADD_NEW_ACCOUNT:
                    Metrix.getInstance().newEvent("bsxvt");
                    //ADD_NEW_ACCOUNT_UNIQUE
                    Metrix.getInstance().newEvent("hnirh");
                    break;
                case TRACKER_TWO_STEP:
                    Metrix.getInstance().newEvent("flrkw");
                    //TWO_STEP_UNIQUE
                    Metrix.getInstance().newEvent("qwqpx");
                    break;
                case TRACKER_ENTRY_PHONE:
                    Metrix.getInstance().newEvent("wnxfo");
                    //ENTRY_PHONE_UNIQUE
                    Metrix.getInstance().newEvent("nfrct");
                    break;
                case TRACKER_MOBILE_BILL_PAY:
                    Metrix.getInstance().newEvent("xnplh");
                    //MOBILE_BILL_PAY_UNIQUE
                    Metrix.getInstance().newEvent("gdouh");
                    break;
                case TRACKER_GAS_BILL_PAY:
                    Metrix.getInstance().newEvent("zdaaq");
                    //GAS_BILL_PAY_UNIQUE
                    Metrix.getInstance().newEvent("wegcl");
                    break;
                case TRACKER_ENTRY_NEW_USER_INFO:
                    Metrix.getInstance().newEvent("xsqei");
                    //ENTRY_NEW_USER_INFO_UNIQUE
                    Metrix.getInstance().newEvent("gmtgx");
                    break;
                case TRACKER_CHANGE_LANGUAGE_FIRST:
                    Metrix.getInstance().newEvent("lmkey");
                    //CHANGE_LANGUAGE_FIRST_UNIQUE
                    Metrix.getInstance().newEvent("gxkzj");
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

    public void initMetrix(ApplicationContext context) {
        try {
            String packageName = context.getPackageName();
            canSendMetrixEvent = packageName != null && packageName.toLowerCase().equals("net.igap");

            if (canSendMetrixEvent) {
                MetrixConfig metrixConfig = new MetrixConfig(context, BuildConfig.METRIX_ID);
                metrixConfig.setFirebaseId(BuildConfig.METRIX_FIREBASE_FIRST_ID, BuildConfig.METRIX_FIREBASE_SECOND_ID, BuildConfig.METRIX_FIREBASE_THEIRD_ID);
                Metrix.onCreate(metrixConfig);
                Metrix.initialize(context, BuildConfig.METRIX_ID);
                if (!BuildConfig.DEBUG) {
                    if (BuildConfig.isStore) {
                        Metrix.getInstance().setStore(BuildConfig.Store);
                    } else {
                        Metrix.getInstance().setDefaultTracker(BuildConfig.TrackCode);
                    }
                    Metrix.getInstance().setAppSecret(BuildConfig.METRIX_SECRET, BuildConfig.METRIX_FIRST_SECRET, BuildConfig.METRIX_SECOND_SECRET, BuildConfig.METRIX_THEIRD_SECRET, BuildConfig.METRIX_FOURTH_SECRET);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
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

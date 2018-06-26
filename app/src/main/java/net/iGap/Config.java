/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap;

import android.content.SharedPreferences;
import android.text.format.DateUtils;

import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.appBarColor;
import static net.iGap.G.attachmentColor;
import static net.iGap.G.canRunReceiver;
import static net.iGap.G.context;
import static net.iGap.G.headerTextColor;
import static net.iGap.G.isDarkTheme;
import static net.iGap.G.notificationColor;
import static net.iGap.G.toggleButtonColor;

public class Config {

    public static final int ACCEPT = 1;
    public static final int REJECT = 0;
    public static final int REALM_SCHEMA_VERSION = 18;
    public static final int REALM_LATEST_MIGRATION_VERSION = REALM_SCHEMA_VERSION - 1;
    public static final int LOOKUP_MAP_RESPONSE_OFFSET = 30000;
    public static final int MAX_TEXT_ATTACHMENT_LENGTH = 200;
    public static final int MAX_TEXT_LENGTH = 4096;
    public static final int IMAGE_CORNER = 7;
    public static final int TRY_CONNECTION_COUNT = 5;
    public static final int ACTION_CHECKING = 500;
    public static final int GROUP_SHOW_ACTIONS_COUNT = 3;
    public static final int LIMIT_GET_HISTORY_LOW = 10;
    public static final int LIMIT_GET_HISTORY_NORMAL = 50;
    public static final int LIMIT_LOAD_ROOM = 50;
    public static final int FAST_START_PAGE_TIME = (int) 20;
    public static final int LOW_START_PAGE_TIME = (int) 25;
    public static final int PHONE_CONTACT_MAX_COUNT_LIMIT = 9999;

    public static final int TIME_OUT_DELAY_MS = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int FAKE_PM_DELAY = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int TIME_OUT_MS = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int ALLOW_RECONNECT_AGAIN_NORMAL = (int) (3 * DateUtils.SECOND_IN_MILLIS);
    public static final int REPEAT_CONNECTION_CHECKING = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int DEFAULT_TIME_OUT = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int INSTANCE_SUCCESSFULLY_CHECKING = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int COUNTER_TIMER = (int) (60 * DateUtils.SECOND_IN_MILLIS);
    public static final int COUNTER_TIMER_DELAY = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int UPDATE_STATUS_TIME = (int) (3 * DateUtils.SECOND_IN_MILLIS);// after this time check that program is in background
    public static final int ACTION_TIME_OUT = (int) (2 * DateUtils.SECOND_IN_MILLIS);
    public static final int GET_MESSAGE_STATE_TIME_OUT = (int) (5 * DateUtils.SECOND_IN_MILLIS);
    public static final int GET_MESSAGE_STATE_TIME_OUT_CHECKING = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int LAST_SEEN_DELAY_CHECKING = (int) (60 * DateUtils.SECOND_IN_MILLIS);
    public static final int GET_CONTACT_LIST_TIME_OUT = (int) (60 * DateUtils.SECOND_IN_MILLIS);
    public static final int HEART_BEAT_CHECKING_TIME_OUT = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int UPDATING_TIME_SHOWING = (int) (2 * DateUtils.SECOND_IN_MILLIS);
    public static final int CONNECTION_OPEN_TIME_OUT = (int) (20 * DateUtils.SECOND_IN_MILLIS);
    public static final int FETCH_CONTACT_TIME_OUT = (int) (5 * DateUtils.SECOND_IN_MILLIS);
    public static final int LAST_SEEN_TIME_OUT = (int) (60 * DateUtils.MINUTE_IN_MILLIS); // after this time show exactly time instead of minutes
    public static final int DEFAULT_BOTH_CHAT_DELETE_TIME = (int) (2 * DateUtils.HOUR_IN_MILLIS);
    public static final String URL_WEBSOCKET = "wss://secure.igap.net/hybrid/";
    public static final String URL_MAP = "https://c.tile.openstreetmap.org/";
    public static final String IGAP_LINK_PREFIX = "https://iGap.net/";
    public static final String PUBLIC_KEY_CLIENT = "-----BEGIN PUBLIC KEY-----\n"
            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo+inlAfd8Qior8IMKaJ+\n"
            + "BREJcEc9J9RhHgh6g/LvHKsnMaiEbAL70jQBQTLpCRu5Cnpj20+isOi++Wtf/pIP\n"
            + "FdJbD/1H+5jS+ja0RA6unp93DnBuYZ2JjV60vF3Ynj6F4Vr1ts5Xg5dJlEaOcOO2\n"
            + "YzOU97ZGP0ozrXIT5S+Y0BC4M9ieQmlGREzt3UZlTBbyUYPS4mMFh88YcT3QTiTA\n"
            + "k897qlJLxkYxVyAgwAD/0ihmWEkBQe9IxwVT/x5/QbixGSl4Zvd+5d+9sTZcSZQS\n"
            + "iJInT4E6DcmgAVYu5jFMWJDTEuurOQZ1W4nbmGyoY1bZXaFoiMPfzy72VIddkoHg\n"
            + "mwIDAQAB\n"
            + "-----END PUBLIC KEY-----\n";
    public static final byte[] SALT = new byte[]{
            -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64, 89
    };
    public static final String BASE64_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsm4sNLgDVqPf0ZxLWH3vkB1mPzHIkGWIJtNelibcTtzhipRv0iHeS3Z0wzeQpwYcMbkWQ81+WtgJwxUujitPOZnHvBex8qQLJ2JH33DvevWOgLDWPKEnKlfdi3Qg09pfO/Bx7eoWznWhRR6ZNjRgzY+P/2AaW77/f3wq3XHbHldM3jUrqwValwrWrkigIR0MFTkaGkg11T9JCFvO/L/FaZCAybuutje+H1nmNav3r8Xv6eBYS0nSVEm0dm5h46ECQi9PIxOCSMJ1McZMRkb8UaCScCAxh6lkD9fgZrOT5XQa8EOSWOwHx"
                    + "+uQWdR0efHyYbdC3A8zoJZjxBVtvVnDYwIDAQAB";


    public static final int CUSTOM = 0;
    public static final int DEFAULT = 1;
    public static final int DARK = 2;
    public static final int RED = 3;
    public static final int PINK = 4;
    public static final int PURPLE = 5;
    public static final int DEEPPURPLE = 6;


    public static String default_appBarColor = "#00B0BF";
    public static String default_notificationColor = "#e51c23";
    public static String default_toggleButtonColor = "#00B0BF";
    public static String default_attachmentColor = "#00B0BF";
    public static String default_headerTextColor = "#00B0BF";
    public static String default_progressColor = "#00B0BF";

    public static String default_dark_appBarColor = "#000000";
    public static String default_dark_notificationColor = "#000000";
    public static String default_dark_toggleButtonColor = "#000000";
    public static String default_dark_attachmentColor = "#000000";
    public static String default_dark_menuBackgroundColor = "#000000";
    public static String default_dark_headerTextColor = "#ffffff";
    public static String default_dark_progressColor = "#ffffff";

    public static String default_red_appBarColor = "#F44336";
    public static String default_Pink_appBarColor = "#E91E63";
    public static String default_purple_appBarColor = "#9C27B0";
    public static String default_deepPurple_appBarColor = "#673AB7";
    public static String lineView = "#52afafaf";


    public static void setThemeColor() {

        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        G.themeColor = preferences.getInt(SHP_SETTING.KEY_THEME_COLOR, Config.DEFAULT);
        G.isDarkTheme = preferences.getBoolean(SHP_SETTING.KEY_THEME_DARK, false);

        switch (G.themeColor) {
            case CUSTOM:
                appBarColor = preferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, Config.default_appBarColor);
                notificationColor = preferences.getString(SHP_SETTING.KEY_NOTIFICATION_COLOR, Config.default_notificationColor);
                toggleButtonColor = preferences.getString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, Config.default_toggleButtonColor);
                attachmentColor = preferences.getString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, Config.default_attachmentColor);
                headerTextColor = preferences.getString(SHP_SETTING.KEY_FONT_HEADER_COLOR, Config.default_headerTextColor);
                G.progressColor = preferences.getString(SHP_SETTING.KEY_PROGRES_COLOR, Config.default_progressColor);

                G.lineBorder = lineView;
                G.backgroundTheme = "#FFFFFF";
                G.backgroundTheme_2 = "#f9f9f9";
                G.textTitleTheme = "#000000";
                G.textSubTheme = "#bbbbbb";
                G.tintImage = "#000000";
                G.logLineTheme = "#e9e9e9";
                G.voteIconTheme = "#696969";


                break;
            case DEFAULT:

                appBarColor = Config.default_appBarColor;
                notificationColor = Config.default_notificationColor;
                toggleButtonColor = Config.default_toggleButtonColor;
                attachmentColor = Config.default_attachmentColor;
                headerTextColor = Config.default_headerTextColor;
                G.progressColor = Config.default_progressColor;


                G.lineBorder = lineView;
                G.backgroundTheme = "#FFFFFF";
                G.backgroundTheme_2 = "#f9f9f9";
                G.textTitleTheme = "#000000";
                G.textSubTheme = "#bbbbbb";
                G.tintImage = "#000000";
                G.logLineTheme = "#e9e9e9";
                G.voteIconTheme = "#696969";

                break;
            case DARK:
                appBarColor = Config.default_dark_appBarColor;
                notificationColor = Config.default_dark_notificationColor;
                toggleButtonColor = Config.default_dark_toggleButtonColor;
                attachmentColor = Config.default_dark_attachmentColor;
                headerTextColor = Config.default_dark_headerTextColor;
                G.progressColor = Config.default_dark_progressColor;


                G.lineBorder = lineView;
                G.backgroundTheme = "#151515";
                G.backgroundTheme_2 = "#000000";
                G.textTitleTheme = "#ffffff";
                G.textSubTheme = "#ffffff";
                G.tintImage = "#ffffff";
                G.logLineTheme = "#4b4b4b";
                G.voteIconTheme = "#cacaca";

                break;
            case RED:
                G.isDarkTheme = false;
                appBarColor = Config.default_red_appBarColor;
                notificationColor = default_red_appBarColor;
                toggleButtonColor = default_red_appBarColor;
                attachmentColor = default_red_appBarColor;
                headerTextColor = default_red_appBarColor;
                G.progressColor = default_red_appBarColor;


                G.lineBorder = Config.default_red_appBarColor;
                G.backgroundTheme = "#FFFFFF";
                G.backgroundTheme_2 = "#f9f9f9";
                G.textTitleTheme = "#000000";
                G.textSubTheme = "#bbbbbb";
                G.tintImage = "#000000";
                G.logLineTheme = "#e9e9e9";
                G.voteIconTheme = "#696969";
                break;

            case PINK:
                G.isDarkTheme = false;
                appBarColor = Config.default_Pink_appBarColor;
                notificationColor = Config.default_Pink_appBarColor;
                toggleButtonColor = Config.default_Pink_appBarColor;
                attachmentColor = Config.default_Pink_appBarColor;
                headerTextColor = Config.default_Pink_appBarColor;
                G.progressColor = Config.default_Pink_appBarColor;


                G.lineBorder = Config.default_Pink_appBarColor;
                G.backgroundTheme = "#FFFFFF";
                G.backgroundTheme_2 = "#f9f9f9";
                G.textTitleTheme = "#000000";
                G.textSubTheme = "#bbbbbb";
                G.tintImage = "#000000";
                G.logLineTheme = "#e9e9e9";
                G.voteIconTheme = "#696969";
                break;
            case PURPLE:
                G.isDarkTheme = false;
                appBarColor = Config.default_purple_appBarColor;
                notificationColor = Config.default_purple_appBarColor;
                toggleButtonColor = Config.default_purple_appBarColor;
                attachmentColor = Config.default_purple_appBarColor;
                headerTextColor = Config.default_purple_appBarColor;
                G.progressColor = Config.default_purple_appBarColor;


                G.lineBorder = Config.default_purple_appBarColor;
                G.backgroundTheme = "#FFFFFF";
                G.backgroundTheme_2 = "#f9f9f9";
                G.textTitleTheme = "#000000";
                G.textSubTheme = "#bbbbbb";
                G.tintImage = "#000000";
                G.logLineTheme = "#e9e9e9";
                G.voteIconTheme = "#696969";
                break;
            case DEEPPURPLE:
                G.isDarkTheme = false;
                appBarColor = Config.default_deepPurple_appBarColor;
                notificationColor = Config.default_deepPurple_appBarColor;
                toggleButtonColor = Config.default_deepPurple_appBarColor;
                attachmentColor = Config.default_deepPurple_appBarColor;
                headerTextColor = Config.default_deepPurple_appBarColor;
                G.progressColor = Config.default_deepPurple_appBarColor;


                G.lineBorder = Config.default_deepPurple_appBarColor;
                G.backgroundTheme = "#FFFFFF";
                G.backgroundTheme_2 = "#f9f9f9";
                G.textTitleTheme = "#000000";
                G.textSubTheme = "#bbbbbb";
                G.tintImage = "#000000";
                G.logLineTheme = "#e9e9e9";
                G.voteIconTheme = "#696969";
                break;


        }

    }

}

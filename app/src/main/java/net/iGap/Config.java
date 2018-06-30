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
import static net.iGap.G.context;
import static net.iGap.G.headerTextColor;
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
    public static final int INDIGO = 7;
    public static final int BLUE = 8;
    public static final int LIGHT_BLUE = 9;
    public static final int CYAN = 10;
    public static final int TEAL = 11;
    public static final int GREEN = 12;
    public static final int LIGHT_GREEN = 13;
    public static final int LIME = 14;
    public static final int YELLLOW = 15;
    public static final int AMBER = 16;
    public static final int ORANGE = 17;
    public static final int DEEP_ORANGE = 18;
    public static final int BROWN = 19;
    public static final int GREY = 20;
    public static final int BLUE_GREY = 21;


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
    public static String default_indigo_appBarColor = "#3F51B5";
    public static String default_blue_appBarColor = "#2196F3";
    public static String default_lightBlue_appBarColor = "#03A9F4";
    public static String default_cyan_appBarColor = "#00BCD4";
    public static String default_teal_appBarColor = "#009688";
    public static String default_green_appBarColor = "#388E3C";
    public static String default_lightGreen_appBarColor = "#689F38";
    public static String default_lime_appBarColor = "#AFB42B";
    public static String default_yellow_appBarColor = "#FBC02D";
    public static String default_amber_appBarColor = "#FFA000";
    public static String default_orange_appBarColor = "#F57C00";
    public static String default_deepOrange_appBarColor = "#E64A19";
    public static String default_brown_appBarColor = "#5D4037";
    public static String default_grey_appBarColor = "#616161";
    public static String default_blueGrey_appBarColor = "#455A64";
    public static String lineView = "#52afafaf";


    public static void setThemeColor() {

        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        G.themeColor = preferences.getInt(SHP_SETTING.KEY_THEME_COLOR, Config.DEFAULT);
        G.isDarkTheme = preferences.getBoolean(SHP_SETTING.KEY_THEME_DARK, false);

        switch (G.themeColor) {
            case CUSTOM:
                setColor(false,
                        preferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, Config.default_appBarColor),
                        preferences.getString(SHP_SETTING.KEY_NOTIFICATION_COLOR, Config.default_notificationColor),
                        preferences.getString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, Config.default_toggleButtonColor),
                        preferences.getString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, Config.default_attachmentColor),
                        preferences.getString(SHP_SETTING.KEY_FONT_HEADER_COLOR, Config.default_headerTextColor),
                        preferences.getString(SHP_SETTING.KEY_PROGRES_COLOR, Config.default_progressColor),
                        lineView,
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#e679dde6",
                        "#FFFFFF",
                        preferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, Config.default_appBarColor),
                        "#000000",
                        preferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, Config.default_appBarColor)
                );

                break;
            case DEFAULT:
                setColor(false,
                        Config.default_appBarColor,
                        Config.default_notificationColor,
                        Config.default_toggleButtonColor,
                        Config.default_attachmentColor,
                        Config.default_headerTextColor,
                        Config.default_progressColor,
                        lineView,
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#e679dde6",
                        "#FFFFFF",
                        "#00BCD4",
                        "#000000",
                        Config.default_appBarColor
                );

                break;
            case DARK:
                setColor(true,
                        Config.default_dark_appBarColor,
                        Config.default_dark_notificationColor,
                        Config.default_dark_toggleButtonColor,
                        Config.default_dark_attachmentColor,
                        Config.default_dark_headerTextColor,
                        Config.default_dark_progressColor,
                        "#313131",
                        "#151515",
                        "#000000",
                        "#ffffff",
                        "#ffffff",
                        "#ffffff",
                        "#4b4b4b",
                        "#cacaca",
                        "#151515",
                        "#c7101010",
                        "#2A2A2A",
                        "#ffffff",
                        "#ffffff"
                );

                break;
            case RED:
                setColor(false,
                        Config.default_red_appBarColor,
                        Config.default_red_appBarColor,
                        Config.default_red_appBarColor,
                        Config.default_red_appBarColor,
                        Config.default_red_appBarColor,
                        Config.default_red_appBarColor,
                        "#ffcdd2",// is set
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#D32F2F",
                        "#FF5252",
                        "#FF5252",
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
            case PINK:
                setColor(false,
                        Config.default_Pink_appBarColor,
                        Config.default_Pink_appBarColor,
                        Config.default_Pink_appBarColor,
                        Config.default_Pink_appBarColor,
                        Config.default_Pink_appBarColor,
                        Config.default_Pink_appBarColor,
                        "#f8bbd0",// is set
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#C2185B",
                        "#FF4081",
                        "#FF4081",
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
            case PURPLE:
                setColor(false,
                        Config.default_purple_appBarColor,
                        Config.default_purple_appBarColor,
                        Config.default_purple_appBarColor,
                        Config.default_purple_appBarColor,
                        Config.default_purple_appBarColor,
                        Config.default_purple_appBarColor,
                        "#e1bee7",// is set
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#7B1FA2",
                        "#E040FB",
                        "#E040FB",
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
            case DEEPPURPLE:
                setColor(false,
                        Config.default_deepPurple_appBarColor,
                        Config.default_deepPurple_appBarColor,
                        Config.default_deepPurple_appBarColor,
                        Config.default_deepPurple_appBarColor,
                        Config.default_deepPurple_appBarColor,
                        Config.default_deepPurple_appBarColor,
                        "#d1c4e9",// is set
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#512DA8",
                        "#7C4DFF",
                        "#7C4DFF",
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
            case INDIGO:
                setColor(false,
                        Config.default_indigo_appBarColor,
                        Config.default_indigo_appBarColor,
                        Config.default_indigo_appBarColor,
                        Config.default_indigo_appBarColor,
                        Config.default_indigo_appBarColor,
                        Config.default_indigo_appBarColor,
                        "#c5cae9",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#303F9F",
                        "#536DFE",
                        "#536DFE",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
            case BLUE:
                setColor(false,
                        Config.default_blue_appBarColor,
                        Config.default_blue_appBarColor,
                        Config.default_blue_appBarColor,
                        Config.default_blue_appBarColor,
                        Config.default_blue_appBarColor,
                        Config.default_blue_appBarColor,
                        "#bbdefb",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#1976D2",
                        "#448AFF",
                        "#03A9F4",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;

            case LIGHT_BLUE:
                setColor(false,
                        Config.default_lightBlue_appBarColor,
                        Config.default_lightBlue_appBarColor,
                        Config.default_lightBlue_appBarColor,
                        Config.default_lightBlue_appBarColor,
                        Config.default_lightBlue_appBarColor,
                        Config.default_lightBlue_appBarColor,
                        "#b3e5fc",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#0288D1",
                        "#03A9F4",
                        "#03A9F4",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;

            case CYAN:
                setColor(false,
                        Config.default_cyan_appBarColor,
                        Config.default_cyan_appBarColor,
                        Config.default_cyan_appBarColor,
                        Config.default_cyan_appBarColor,
                        Config.default_cyan_appBarColor,
                        Config.default_cyan_appBarColor,
                        "#b2ebf2",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#0097A7",
                        "#00BCD4",
                        "#00BCD4",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
            case TEAL:
                setColor(false,
                        Config.default_teal_appBarColor,
                        Config.default_teal_appBarColor,
                        Config.default_teal_appBarColor,
                        Config.default_teal_appBarColor,
                        Config.default_teal_appBarColor,
                        Config.default_teal_appBarColor,
                        "#b2dfdb",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#00796B",
                        "#009688",
                        "#009688",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
            case GREEN:
                setColor(false,
                        Config.default_green_appBarColor,
                        Config.default_green_appBarColor,
                        Config.default_green_appBarColor,
                        Config.default_green_appBarColor,
                        Config.default_green_appBarColor,
                        Config.default_green_appBarColor,
                        "#c8e6c9",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#388E3C",
                        "#4CAF50",
                        "#4CAF50",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
            case LIGHT_GREEN:
                setColor(false,
                        Config.default_lightGreen_appBarColor,
                        Config.default_lightGreen_appBarColor,
                        Config.default_lightGreen_appBarColor,
                        Config.default_lightGreen_appBarColor,
                        Config.default_lightGreen_appBarColor,
                        Config.default_lightGreen_appBarColor,
                        "#dcedc8",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#689F38",
                        "#8BC34A",
                        "#8BC34A",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
            case LIME:
                setColor(false,
                        Config.default_lime_appBarColor,
                        Config.default_lime_appBarColor,
                        Config.default_lime_appBarColor,
                        Config.default_lime_appBarColor,
                        Config.default_lime_appBarColor,
                        Config.default_lime_appBarColor,
                        "#f0f4c3",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#AFB42B",
                        "#CDDC39",
                        "#CDDC39",//fab bottom
                        "#212121",
                        "#212121"
                );
                break;

            case YELLLOW:
                setColor(false,
                        Config.default_yellow_appBarColor,
                        Config.default_yellow_appBarColor,
                        Config.default_yellow_appBarColor,
                        Config.default_yellow_appBarColor,
                        Config.default_yellow_appBarColor,
                        Config.default_yellow_appBarColor,
                        "#fff9c4",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#FBC02D",
                        "#FFEB3B",
                        "#FFEB3B",//fab bottom
                        "#212121",
                        "#212121"
                );
                break;
            case AMBER:
                setColor(false,
                        Config.default_amber_appBarColor,
                        Config.default_amber_appBarColor,
                        Config.default_amber_appBarColor,
                        Config.default_amber_appBarColor,
                        Config.default_amber_appBarColor,
                        Config.default_amber_appBarColor,
                        "#ffecb3",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#FFA000",
                        "#FFC107",
                        "#FFC107",//fab bottom
                        "#212121",
                        "#212121"
                );
                break;
            case ORANGE:
                setColor(false,
                        Config.default_orange_appBarColor,
                        Config.default_orange_appBarColor,
                        Config.default_orange_appBarColor,
                        Config.default_orange_appBarColor,
                        Config.default_orange_appBarColor,
                        Config.default_orange_appBarColor,
                        "#ffe0b2",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#F57C00",
                        "#FF9800",
                        "#FF9800",//fab bottom
                        "#212121",
                        "#212121"
                );
                break;

            case DEEP_ORANGE:
                setColor(false,
                        Config.default_deepOrange_appBarColor,
                        Config.default_deepOrange_appBarColor,
                        Config.default_deepOrange_appBarColor,
                        Config.default_deepOrange_appBarColor,
                        Config.default_deepOrange_appBarColor,
                        Config.default_deepOrange_appBarColor,
                        "#ffccbc",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#E64A19",
                        "#FF5722",
                        "#FF5722",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;

            case BROWN:
                setColor(false,
                        Config.default_brown_appBarColor,
                        Config.default_brown_appBarColor,
                        Config.default_brown_appBarColor,
                        Config.default_brown_appBarColor,
                        Config.default_brown_appBarColor,
                        Config.default_brown_appBarColor,
                        "#d7ccc8",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#5D4037",
                        "#795548",
                        "#795548",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
            case GREY:
                setColor(false,
                        Config.default_grey_appBarColor,
                        Config.default_grey_appBarColor,
                        Config.default_grey_appBarColor,
                        Config.default_grey_appBarColor,
                        Config.default_grey_appBarColor,
                        Config.default_grey_appBarColor,
                        "#f5f5f5",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#616161",
                        "#9E9E9E",
                        "#9E9E9E",//fab bottom
                        "#212121",
                        "#212121"
                );
                break;
            case BLUE_GREY:
                setColor(false,
                        Config.default_blueGrey_appBarColor,
                        Config.default_blueGrey_appBarColor,
                        Config.default_blueGrey_appBarColor,
                        Config.default_blueGrey_appBarColor,
                        Config.default_blueGrey_appBarColor,
                        Config.default_blueGrey_appBarColor,
                        "#cfd8dc",// line
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#696969",
                        "#455A64",
                        "#607D8B",
                        "#607D8B",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF"
                );
                break;
        }
    }

    private static void setColor(boolean isDarkTheme, String... color) {

        G.isDarkTheme = isDarkTheme;

        appBarColor = color[0];
        notificationColor = color[1];
        toggleButtonColor = color[2];
        attachmentColor = color[3];
        headerTextColor = color[4];
        G.progressColor = color[5];

        G.lineBorder = color[6];// ok
        G.backgroundTheme = color[7];
        G.backgroundTheme_2 = color[8];
        G.textTitleTheme = color[9];
        G.textSubTheme = color[10];
        G.tintImage = color[11];
        G.logLineTheme = color[12];
        G.voteIconTheme = color[13];
        G.bubbleChatSend = color[14];
        G.bubbleChatReceive = color[15];
        G.fabBottom = color[16];
        G.textBubble = color[17];
        G.txtIconCheck = color[18];

    }

}

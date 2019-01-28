package net.iGap;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.one.EmojiOneProvider;

import net.iGap.module.SHP_SETTING;

import static net.iGap.G.appBarColor;
import static net.iGap.G.attachmentColor;
import static net.iGap.G.context;
import static net.iGap.G.headerTextColor;
import static net.iGap.G.notificationColor;
import static net.iGap.G.toggleButtonColor;

/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */
public class Theme extends Application {

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
    public static final int BLUE_GREY_COMPLETE = 22;
    public static final int INDIGO_COMPLETE = 23;
    public static final int BROWN_COMPLETE = 24;
    public static final int TEAL_COMPLETE = 25;
    public static final int GREY_COMPLETE = 26;


    public static String default_appBarColor = "#9dc756";
    public static String default_notificationColor = "#e05353";
    public static String default_toggleButtonColor = "#00B0BF";
    public static String default_attachmentColor = "#00B0BF";
    public static String default_headerTextColor = "#00B0BF";
    public static String default_progressColor = "#00B0BF";
    public static String default_linkColor = "#303F9F";
    public static String default_bubbleChatMusicColor = "#bfefef";
    public static String default_bubbleChatSendColor = "#809dc756";
    public static String default_bubbleChatReceiveColor = "#cce5e1dc";
    public static String default_backgroundThemeMsg = "#00f4f0e8";
    public static String default_backgroundThemeMsgPined = "#1a9dc756";
    public static String default_textTitleTheme = "#2c363f";
    public static String default_textSubTheme = "#FF616161";
    public static String default_tintImage = default_textSubTheme;
    public static String default_logLineTheme = "#e9e9e9";
    public static String default_voteIconTheme = "#FFFFFF";
    public static String default_textBubbleColor = "#686868";
    public static String default_txtIconCheckColor = default_tintImage;
    public static String default_textChatMusicColor = "#212121";
    public static String default_roomMessageTypeColor = "#9dc756";
    public static String default_roomSenderTextColor = default_textSubTheme;
    public static String default_SeenTickColor = default_appBarColor;

    public static String default_dark_appBarColor = "#2a3d3d";
    public static String default_dark_notificationColor = "#c4d838";
    public static String default_dark_toggleButtonColor = "#000000";//
    public static String default_dark_attachmentColor = "#ffffff";
    public static String default_dark_headerTextColor = "#ffffff";
    public static String default_dark_progressColor = "#ffffff";
    public static String default_dark_linkColor = "#00BCD4";
    public static String default_dark_bubbleChatMusicColor = "#313131";//
    public static String default_dark_bubbleChatSendColor = "#33749e85";
    public static String default_dark_bubbleChatReceiveColor = "#33ddd1c5";
    public static String default_dark_backgroundThemeMsg = "#2c363f";
    public static String default_dark_backgroundThemeMsgPined = "#3314191c";
    public static String default_dark_textTitleTheme = "#cccccc";
    public static String default_dark_textSubTheme = "#686868";
    public static String default_dark_tintImage = default_dark_textSubTheme;
    public static String default_dark_logLineTheme = "#2c363f";//
    public static String default_dark_voteIconTheme = "#cacaca";
    public static String default_dark_textBubbleColor = "#cccccc";
    public static String default_dark_txtIconCheckColor = default_dark_tintImage;
    public static String default_dark_textChatMusicColor = "#cccccc";
    public static String default_dark_roomMessageTypeColor = "#809dc756";
    public static String default_dark_roomSenderTextColor = default_dark_textSubTheme;
    public static String default_dark_SeenTickColor = default_dark_notificationColor;

    public static String default_dark_background = "#2c363f";

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
    public static String lineView_dark = "#313131";


    public static void setThemeColor() {

        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        G.themeColor = preferences.getInt(SHP_SETTING.KEY_THEME_COLOR, DEFAULT);
        G.isDarkTheme = preferences.getBoolean(SHP_SETTING.KEY_THEME_DARK, false);

        /*if (G.themeColor == DARK) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }*/
        EmojiManager.install(new EmojiOneProvider());


        switch (G.themeColor) {
            case CUSTOM:
                setColor(false,
                        preferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, default_appBarColor),
                        preferences.getString(SHP_SETTING.KEY_NOTIFICATION_COLOR, default_notificationColor),
                        preferences.getString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, default_toggleButtonColor),
                        preferences.getString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, default_attachmentColor),
                        preferences.getString(SHP_SETTING.KEY_FONT_HEADER_COLOR, default_headerTextColor),
                        preferences.getString(SHP_SETTING.KEY_PROGRES_COLOR, default_progressColor),
                        lineView,
                        default_backgroundThemeMsg,
                        default_backgroundThemeMsgPined,
                        default_textTitleTheme,
                        default_textSubTheme,
                        default_tintImage,
                        default_logLineTheme,
                        default_voteIconTheme,
                        default_bubbleChatSendColor,
                        default_bubbleChatReceiveColor,
                        preferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, default_appBarColor),
                        default_textBubbleColor,
                        default_txtIconCheckColor,
                        default_bubbleChatMusicColor,
                        default_linkColor,
                        default_textChatMusicColor,
                        default_roomMessageTypeColor,
                        default_roomSenderTextColor,
                        default_SeenTickColor

                );

                break;
            case DEFAULT:
                setColor(false,
                        default_appBarColor,
                        default_notificationColor,
                        default_toggleButtonColor,
                        default_attachmentColor,
                        default_headerTextColor,
                        default_progressColor,
                        lineView,
                        default_backgroundThemeMsg,
                        default_backgroundThemeMsgPined,
                        default_textTitleTheme,
                        default_textSubTheme,
                        default_tintImage,
                        default_logLineTheme,
                        default_voteIconTheme,
                        default_bubbleChatSendColor,
                        default_bubbleChatReceiveColor,
                        default_appBarColor,
                        default_textBubbleColor,
                        default_txtIconCheckColor,
                        default_bubbleChatMusicColor,
                        default_linkColor,
                        default_textChatMusicColor,
                        default_roomMessageTypeColor,
                        default_roomSenderTextColor,
                        default_SeenTickColor
                );

                break;
            case DARK:
                setColor(true,
                        default_dark_appBarColor,
                        default_dark_notificationColor,
                        default_dark_toggleButtonColor,
                        default_dark_attachmentColor,
                        default_dark_headerTextColor,
                        default_dark_progressColor,
                        lineView_dark,
                        default_dark_backgroundThemeMsg,
                        default_dark_backgroundThemeMsgPined,
                        default_dark_textTitleTheme,
                        default_dark_textSubTheme,
                        default_dark_tintImage,
                        default_dark_logLineTheme,
                        default_dark_voteIconTheme,
                        default_dark_bubbleChatSendColor,
                        default_dark_bubbleChatReceiveColor,
                        default_dark_appBarColor,
                        default_dark_textBubbleColor,
                        default_dark_txtIconCheckColor,
                        default_dark_bubbleChatMusicColor,
                        default_dark_linkColor,
                        default_dark_textChatMusicColor,
                        default_dark_roomMessageTypeColor,
                        default_dark_roomSenderTextColor,
                        default_dark_SeenTickColor
                );

                break;
            case RED:
                setColor(false,
                        default_red_appBarColor,
                        default_red_appBarColor,
                        default_red_appBarColor,
                        default_red_appBarColor,
                        default_red_appBarColor,
                        default_red_appBarColor,
                        "#ffcdd2",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#ef9a9a",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#D32F2F",//bubbleChatSendColor,
                        "#FF5252",//bubbleChatReceiveColor,
                        "#FF5252",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#ffcdd2",//bubbleChatMusicColor,
                        "#283593",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_red_appBarColor
                );
                break;
            case PINK:
                setColor(false,
                        default_Pink_appBarColor,
                        default_Pink_appBarColor,
                        default_Pink_appBarColor,
                        default_Pink_appBarColor,
                        default_Pink_appBarColor,
                        default_Pink_appBarColor,
                        "#f8bbd0",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#f48fb1",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#C2185B",//bubbleChatSendColor,
                        "#FF4081",//bubbleChatReceiveColor,
                        "#FF4081",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#f8bbd0",//bubbleChatMusicColor,
                        "#283593",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_Pink_appBarColor
                );
                break;
            case PURPLE:
                setColor(false,
                        default_purple_appBarColor,
                        default_purple_appBarColor,
                        default_purple_appBarColor,
                        default_purple_appBarColor,
                        default_purple_appBarColor,
                        default_purple_appBarColor,
                        "#e1bee7",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#ce93d8",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#7B1FA2",//bubbleChatSendColor,
                        "#E040FB",//bubbleChatReceiveColor,
                        "#E040FB",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#e1bee7",//bubbleChatMusicColor,
                        "#303F9F",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_purple_appBarColor
                );
                break;
            case DEEPPURPLE:
                setColor(false,
                        default_deepPurple_appBarColor,
                        default_deepPurple_appBarColor,
                        default_deepPurple_appBarColor,
                        default_deepPurple_appBarColor,
                        default_deepPurple_appBarColor,
                        default_deepPurple_appBarColor,
                        "#d1c4e9",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#b39ddb",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#512DA8",//bubbleChatSendColor,
                        "#7C4DFF",//bubbleChatReceiveColor,
                        "#7C4DFF",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#d1c4e9",//bubbleChatMusicColor,
                        "#303F9F",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_deepPurple_appBarColor
                );
                break;
            case INDIGO:
                setColor(false,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        "#c5cae9",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#9fa8da",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#303F9F",//bubbleChatSendColor,
                        "#536DFE",//bubbleChatReceiveColor,
                        "#536DFE",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#c5cae9",//bubbleChatMusicColor,
                        "#00BCD4",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_indigo_appBarColor
                );
                break;
            case BLUE:
                setColor(false,
                        default_blue_appBarColor,
                        default_blue_appBarColor,
                        default_blue_appBarColor,
                        default_blue_appBarColor,
                        default_blue_appBarColor,
                        default_blue_appBarColor,
                        "#bbdefb",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#90caf9",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#1976D2",//bubbleChatSendColor,
                        "#448AFF",//bubbleChatReceiveColor,
                        "#03A9F4",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#bbdefb",//bubbleChatMusicColor,
                        "#283593",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_blue_appBarColor
                );
                break;

            case LIGHT_BLUE:
                setColor(false,
                        default_lightBlue_appBarColor,
                        default_lightBlue_appBarColor,
                        default_lightBlue_appBarColor,
                        default_lightBlue_appBarColor,
                        default_lightBlue_appBarColor,
                        default_lightBlue_appBarColor,
                        "#b3e5fc",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#81d4fa",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#0288D1",//bubbleChatSendColor,
                        "#03A9F4",//bubbleChatReceiveColor,
                        "#03A9F4",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#b3e5fc",//bubbleChatMusicColor,
                        "#283593",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_lightBlue_appBarColor
                );
                break;

            case CYAN:
                setColor(false,
                        default_cyan_appBarColor,
                        default_cyan_appBarColor,
                        default_cyan_appBarColor,
                        default_cyan_appBarColor,
                        default_cyan_appBarColor,
                        default_cyan_appBarColor,
                        "#b2ebf2",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#81d4fa",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#0097A7",//bubbleChatSendColor,
                        "#00BCD4",//bubbleChatReceiveColor,
                        "#00BCD4",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#b2ebf2",//bubbleChatMusicColor,
                        "#283593",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_cyan_appBarColor
                );
                break;
            case TEAL:
                setColor(false,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        "#b2dfdb",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#80cbc4",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#00796B",//bubbleChatSendColor,
                        "#009688",//bubbleChatReceiveColor,
                        "#009688",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#b2dfdb",//bubbleChatMusicColor,
                        "#303F9F",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_teal_appBarColor
                );
                break;
            case GREEN:
                setColor(false,
                        default_green_appBarColor,
                        default_green_appBarColor,
                        default_green_appBarColor,
                        default_green_appBarColor,
                        default_green_appBarColor,
                        default_green_appBarColor,
                        "#c8e6c9",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#a5d6a7",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#388E3C",//bubbleChatSendColor,
                        "#4CAF50",//bubbleChatReceiveColor,
                        "#4CAF50",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#c8e6c9",//bubbleChatMusicColor,
                        "#283593",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_green_appBarColor
                );
                break;
            case LIGHT_GREEN:
                setColor(false,
                        default_lightGreen_appBarColor,
                        default_lightGreen_appBarColor,
                        default_lightGreen_appBarColor,
                        default_lightGreen_appBarColor,
                        default_lightGreen_appBarColor,
                        default_lightGreen_appBarColor,
                        "#dcedc8",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#c5e1a5",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#689F38",//bubbleChatSendColor,
                        "#8BC34A",//bubbleChatReceiveColor,
                        "#8BC34A",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#dcedc8",//bubbleChatMusicColor,
                        "#283593",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_lightGreen_appBarColor
                );
                break;
            case LIME:
                setColor(false,
                        default_lime_appBarColor,
                        default_lime_appBarColor,
                        default_lime_appBarColor,
                        default_lime_appBarColor,
                        default_lime_appBarColor,
                        default_lime_appBarColor,
                        "#f0f4c3",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#e6ee9c",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#AFB42B",//bubbleChatSendColor,
                        "#CDDC39",//bubbleChatReceiveColor,
                        "#CDDC39",//fab bottom appBarColor,
                        "#212121",//textBubbleColor,
                        "#212121",//txtIconCheckColor,
                        "#f0f4c3",//bubbleChatMusicColor,
                        "#283593",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_lime_appBarColor
                );
                break;

            case YELLLOW:
                setColor(false,
                        default_yellow_appBarColor,
                        default_yellow_appBarColor,
                        default_yellow_appBarColor,
                        default_yellow_appBarColor,
                        default_yellow_appBarColor,
                        default_yellow_appBarColor,
                        "#fff9c4",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#fff59d",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#FBC02D",//bubbleChatSendColor,
                        "#FFEB3B",//bubbleChatReceiveColor,
                        "#FFEB3B",//fab bottom appBarColor,
                        "#212121",//textBubbleColor,
                        "#212121",//txtIconCheckColor,
                        "#fff9c4",//bubbleChatMusicColor,
                        "#00BCD4",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_yellow_appBarColor
                );
                break;
            case AMBER:
                setColor(false,
                        default_amber_appBarColor,
                        default_amber_appBarColor,
                        default_amber_appBarColor,
                        default_amber_appBarColor,
                        default_amber_appBarColor,
                        default_amber_appBarColor,
                        "#ffecb3",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#ffe082",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#FFA000",//bubbleChatSendColor,
                        "#FFC107",//bubbleChatReceiveColor,
                        "#FFC107",//fab bottom appBarColor,
                        "#212121",//textBubbleColor,
                        "#212121",//txtIconCheckColor,
                        "#ffecb3",//bubbleChatMusicColor,
                        "#283593",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_amber_appBarColor
                );
                break;
            case ORANGE:
                setColor(false,
                        default_orange_appBarColor,
                        default_orange_appBarColor,
                        default_orange_appBarColor,
                        default_orange_appBarColor,
                        default_orange_appBarColor,
                        default_orange_appBarColor,
                        "#ffe0b2",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#ffcc80",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#F57C00",//bubbleChatSendColor,
                        "#FF9800",//bubbleChatReceiveColor,
                        "#FF9800",//fab bottom appBarColor,
                        "#212121",//textBubbleColor,
                        "#212121",//txtIconCheckColor,
                        "#ffe0b2",//bubbleChatMusicColor,
                        "#283593",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_orange_appBarColor
                );
                break;

            case DEEP_ORANGE:
                setColor(false,
                        default_deepOrange_appBarColor,
                        default_deepOrange_appBarColor,
                        default_deepOrange_appBarColor,
                        default_deepOrange_appBarColor,
                        default_deepOrange_appBarColor,
                        default_deepOrange_appBarColor,
                        "#ffccbc",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#ffab91",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#E64A19",//bubbleChatSendColor,
                        "#FF5722",//bubbleChatReceiveColor,
                        "#FF5722",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#ffccbc",//bubbleChatMusicColor,
                        "#536DFE",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_deepOrange_appBarColor

                );
                break;

            case BROWN:
                setColor(false,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        "#d7ccc8",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#bcaaa4",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#5D4037",//bubbleChatSendColor,
                        "#795548",//bubbleChatReceiveColor,
                        "#795548",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#d7ccc8",//bubbleChatMusicColor,
                        "#303F9F",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_brown_appBarColor
                );
                break;
            case GREY:
                setColor(false,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        "#f5f5f5",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#e0e0e0",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#616161",//bubbleChatSendColor,
                        "#9E9E9E",//bubbleChatReceiveColor,
                        "#9E9E9E",//fab bottom appBarColor,
                        "#ffffff",//textBubbleColor,
                        "#212121",//txtIconCheckColor,
                        "#f5f5f5",//bubbleChatMusicColor,
                        "#536DFE",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_grey_appBarColor

                );
                break;
            case BLUE_GREY:
                setColor(false,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        "#cfd8dc",//lineView,
                        "#FFFFFF",//backgroundThemeMsg,
                        "#b0bec5",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#455A64",//bubbleChatSendColor,
                        "#607D8B",//bubbleChatReceiveColor,
                        "#607D8B",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#cfd8dc",//bubbleChatMusicColor,
                        "#303F9F",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_blueGrey_appBarColor
                );
                break;
            case BLUE_GREY_COMPLETE:
                setColor(false,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        "#607D8B",//lineView,
                        "#CFD8DC",//backgroundThemeMsg,
                        "#b0bec5",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#455A64",//bubbleChatSendColor,
                        "#607D8B",//bubbleChatReceiveColor,
                        "#607D8B",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#cfd8dc",//bubbleChatMusicColor,
                        "#00bcd4",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_blueGrey_appBarColor
                );
                break;

            case INDIGO_COMPLETE:
                setColor(false,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        "#3f51b5",//lineView,
                        "#C5CAE9",//backgroundThemeMsg,
                        "#9fa8da",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#303F9F",//bubbleChatSendColor,
                        "#3f51b5",//bubbleChatReceiveColor,
                        "#3f51b5",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#C5CAE9",//bubbleChatMusicColor,
                        "#448AFF",//linkColor,
                        "#212121" , //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_indigo_appBarColor

                );
                break;
            case BROWN_COMPLETE:
                setColor(false,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        "#795548",//lineView,
                        "#D7CCC8",//backgroundThemeMsg,
                        "#bcaaa4",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,?
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#5D4037",//bubbleChatSendColor,
                        "#795548",//bubbleChatReceiveColor,
                        "#795548",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,?
                        "#D7CCC8",//bubbleChatMusicColor,
                        "#00bcd4",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_brown_appBarColor
                );
                break;
            case TEAL_COMPLETE:
                setColor(false,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        "#009688",//lineView,
                        "#B2DFDB",//backgroundThemeMsg,
                        "#80cbc4",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#00796B",//bubbleChatSendColor,
                        "#009688",//bubbleChatReceiveColor,
                        "#009688",//fab bottom appBarColor,
                        "#FFFFFF",//textBubbleColor,
                        "#FFFFFF",//txtIconCheckColor,
                        "#B2DFDB",//bubbleChatMusicColor,
                        "#303F9F",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_teal_appBarColor
                );
                break;

            case GREY_COMPLETE:
                setColor(false,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        "#e0e0e0",//lineView,
                        "#F5F5F5",//backgroundThemeMsg,
                        "#e0e0e0",//backgroundThemeMsgPined
                        "#000000",//textTitleTheme,
                        "#FF616161",//textSubTheme
                        "#000000",//tintImage,
                        "#e9e9e9",//logLineTheme,
                        "#FFFFFF",//voteIconTheme,
                        "#616161",//bubbleChatSendColor,
                        "#9E9E9E",//bubbleChatReceiveColor,
                        "#9E9E9E",//fab bottom appBarColor,
                        "#ffffff",//textBubbleColor,
                        "#212121",//txtIconCheckColor,
                        "#F5F5F5",//bubbleChatMusicColor,
                        "#2196f3",//linkColor,
                        "#212121", //textChatMusicColor
                        "#007eff", // messageTypeColor
                        "#FF616161", //roomSenderTextColor
                        default_grey_appBarColor
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
        G.bubbleChatMusic = color[19];
        G.linkColor = color[20];
        G.textChatMusic = color[21];
        G.roomMessageTypeColor = color[22];
        G.roomSenderTextColor = color[23];
        G.SeenTickColor = color[24];

    }

}

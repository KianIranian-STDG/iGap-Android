package net.iGap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

import androidx.core.graphics.drawable.DrawableCompat;

import net.iGap.model.ThemeModel;
import net.iGap.module.SHP_SETTING;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
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
 * The idea of the kianiranian Company - http://www.kianiranian.com/
 * All rights reserved.
 */
public class Theme {

    public static final int DEFAULT = 1;
    public static final int DARK = 2;
    public static final int RED = 3;
    public static final int PINK = 4;
    public static final int PURPLE = 5;
    public static final int BLUE = 8;
    public static final int GREEN = 12;
    public static final int AMBER = 16;
    public static final int ORANGE = 17;
    public static final int GREY = 20;


    public static String default_appBarColor = "#45B321";
    public static String default_notificationColor = "#e05353";
    public static String default_toggleButtonColor = "#00B0BF";
    public static String default_attachmentColor = default_appBarColor;
    public static String default_headerTextColor = "#00B0BF";
    public static String default_progressColor = "#45B321";
    public static String default_linkColor = "#303F9F";
    public static String default_bubbleChatMusicColor = "#bfefef";
    public static String default_bubbleChatSendColor = "#ccdba1";
    public static String default_bubbleChatReceiveColor = "#e7e4dd";
    public static String default_backgroundThemeMsg = "#00f4f0e8";
    public static String default_backgroundThemeMsgPined = "#1a9dc756";
    public static String default_textTitleTheme = "#2c363f";
    public static String default_textSubTheme = "#FF616161";
    public static String default_tintImage = default_textSubTheme;
    public static String default_logLineTheme = "#e9e9e9";
    public static String default_textBubbleColor = "#686868";
    public static String default_voteIconTheme = default_textBubbleColor;
    public static String default_txtIconCheckColor = default_tintImage;
    public static String default_textChatMusicColor = "#212121";
    public static String default_roomMessageTypeColor = "#9dc756";
    public static String default_roomSenderTextColor = default_textSubTheme;
    public static String default_SeenTickColor = default_appBarColor;

    public static String default_dark_appBarColor = "#383838";
    public static String default_dark_notificationColor = "#c4d838";
    public static String default_dark_toggleButtonColor = "#000000";//
    public static String default_dark_attachmentColor = "#cccccc";
    public static String default_dark_headerTextColor = "#ffffff";
    public static String default_dark_progressColor = "#45B321";
    public static String default_dark_linkColor = "#00BCD4";
    public static String default_dark_bubbleChatMusicColor = "#313131";//
    public static String default_dark_bubbleChatSendColor = "#394b4b";
    public static String default_dark_bubbleChatReceiveColor = "#4d5558";
    public static String default_dark_backgroundThemeMsg = "#2f2f2f";
    public static String default_dark_backgroundThemeMsgPined = "#3314191c";
    public static String default_dark_textTitleTheme = "#cccccc";
    public static String default_dark_textSubTheme = "#aaaaaa";
    public static String default_dark_tintImage = default_dark_textSubTheme;
    public static String default_dark_logLineTheme = "#2c363f";//
    public static String default_dark_textBubbleColor = "#cccccc";
    public static String default_dark_voteIconTheme = default_dark_textBubbleColor;
    public static String default_dark_txtIconCheckColor = default_dark_tintImage;
    public static String default_dark_textChatMusicColor = "#cccccc";
    public static String default_dark_roomMessageTypeColor = "#809dc756";
    public static String default_dark_roomSenderTextColor = default_dark_textSubTheme;
    public static String default_dark_SeenTickColor = default_dark_notificationColor;

    public static String default_dark_background = "#2f2f2f";

    public static String default_red_appBarColor = "#F44336";
    public static String default_Pink_appBarColor = "#f68787";
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

        switch (G.themeColor) {
            case DEFAULT:
                setColor(
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
                setColor(
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
                setColor(
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
                setColor(
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
                setColor(
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
            case BLUE:
                setColor(
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
            case GREEN:
                setColor(
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
            case AMBER:
                setColor(
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
                setColor(
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
            case GREY:
                setColor(
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
        }
    }

    private static void setColor(String... color) {

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

    public int getTheme(@NotNull Context context) {
        switch (context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getInt(SHP_SETTING.KEY_THEME_COLOR, Theme.DEFAULT)) {
            case DARK:
                return R.style.iGapDarkTheme;
            case RED:
                return R.style.iGapRedTheme;
            case PINK:
                return R.style.iGapPinkTheme;
            case PURPLE:
                return R.style.iGapPurpleTheme;
            case BLUE:
                return R.style.iGapBlueTheme;
            case GREEN:
                return R.style.iGapGreenTheme;
            case AMBER:
                return R.style.iGapAmberTheme;
            case ORANGE:
                return R.style.iGapOrangeTheme;
            case GREY:
                return R.style.iGapGrayTheme;
            default:
                return R.style.iGapLightTheme;
        }
    }

    public int getColor(int themeId) {
        switch (themeId) {
            case AMBER:
                return R.color.amber;
            case BLUE:
                return R.color.blue;
            case DARK:
                return R.color.navigation_dark_mode_bg;
            case RED:
                return R.color.redTheme;
            case PINK:
                return R.color.pink;
            case PURPLE:
                return R.color.purple;
            case GREEN:
                return R.color.greenThem;
            case ORANGE:
                return R.color.orange;
            case GREY:
                return R.color.grayTheme;
            case DEFAULT:
            default:
                return R.color.green;
        }
    }

    public List<ThemeModel> getThemeList() {
        List<ThemeModel> themeModelList = new ArrayList<>();
        themeModelList.add(new ThemeModel(Theme.DEFAULT, R.string.default_theme_title));
        themeModelList.add(new ThemeModel(Theme.DARK, R.string.dark_theme_title));
        themeModelList.add(new ThemeModel(Theme.RED, R.string.red_theme_title));
        themeModelList.add(new ThemeModel(Theme.PINK, R.string.pink_theme_title));
        themeModelList.add(new ThemeModel(Theme.PURPLE, R.string.purple_theme_title));
        themeModelList.add(new ThemeModel(Theme.BLUE, R.string.blue_theme_title));
        themeModelList.add(new ThemeModel(Theme.GREEN, R.string.green_theme_title));
        themeModelList.add(new ThemeModel(Theme.AMBER, R.string.amber_theme_title));
        themeModelList.add(new ThemeModel(Theme.ORANGE, R.string.orange_theme_title));
        themeModelList.add(new ThemeModel(Theme.GREY, R.string.gray_theme_title));
        return themeModelList;
    }

    public int getReceivedMessageColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapReceivedMessageTextColor);
    }

    public int getReceivedMessageBackgroundColor(Context context) {
        return getColorFromAttr(context, R.attr.colorPrimaryLight);
    }

    public int getReceivedMessageOtherTextColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapReceivedOtherTextColor);
    }

    public int getSendChatBubbleColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapSendMessageBubbleColor);
    }

    public int getReceivedChatBubbleColor(Context context) {
        return getColorFromAttr(context, R.attr.colorPrimaryLight);
    }

    public int getPrimaryTextIconColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapPrimaryIconTextColor);
    }

    public int getSendMessageOtherTextColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapSendMessageOtherTextColor);
    }

    public int getSendMessageTextColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapSendMessageTextColor);
    }

    public int getMessageVerticalLineColor(Context context) {
        return getColorFromAttr(context, R.attr.colorAccentDark);
    }

    public int getDividerColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapDividerLine);
    }

    public int getPrimaryTextColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapPrimaryTextColor);
    }

    public int getPrimaryColor(Context context) {
        return getColorFromAttr(context, R.attr.colorPrimary);
    }

    public int getAccentColor(Context context) {
        return getColorFromAttr(context, R.attr.colorAccent);
    }

    public int getPrimaryDarkColor(Context context) {
        return getColorFromAttr(context, R.attr.colorPrimaryDark);
    }

    public int getRootColor(Context context) {
        return getColorFromAttr(context, R.attr.rootBackgroundColor);
    }

    public int getForwardFromTextColor(Context context) {
        return getColorFromAttr(context, R.attr.colorAccentDark);
    }

    public int getTitleTextColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapTitleTextColor);
    }

    public int getSubTitleColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapSubtitleTextColor);
    }

    public int getLinkColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapLinkColor);
    }

    private int getColorFromAttr(@NotNull Context context, int attrResId) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{attrResId});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public Drawable tintDrawable(Drawable drawable, Context context, int colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(getColorFromAttr(context, colors)));
        return wrappedDrawable;
    }

    // for under lollipop
    private int getDrawableAttr(@NotNull Context context, int attrResId) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{attrResId});
        int drawableResId = a.getResourceId(0, 0);
        a.recycle();
        return drawableResId;
    }

    public int getToolbarDrawable(Context context) {
        return getDrawableAttr(context, R.attr.iGapToolbarBackground);
    }

    public int getToolbarDrawableSharpe(Context context) {
        return getDrawableAttr(context, R.attr.iGapToolbarBackgroundSharp);
    }

    public int getReceivedReplay(Context context) {
        return getDrawableAttr(context, R.attr.iGapReceivedReplayBackground);
    }

    public int getSendReplay(Context context) {
        return getDrawableAttr(context, R.attr.iGapSendReplayBackground);
    }

    public int getCardToCardBackground(Context context) {
        return getDrawableAttr(context, R.attr.iGapCardToCardBackground);
    }

    public int getCardToCardButtonBackground(Context context) {
        return getDrawableAttr(context, R.attr.iGapCardToCardButtonBackground);
    }

    public int getCardToCardIconBackground(Context context) {
        return getDrawableAttr(context, R.attr.iGapCardToCardIconBackground);
    }

    public int getFastScrollerBackground(Context context) {
        return getDrawableAttr(context, R.attr.iGapScrollerHandler);
    }

    public int getButtonSelectorBackground(Context context) {
        return getDrawableAttr(context, R.attr.iGapButtonSelector);
    }

    public static boolean isUnderLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
    // for under lollipop

}

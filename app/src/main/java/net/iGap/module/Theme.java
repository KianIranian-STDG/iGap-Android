package net.iGap.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.StateSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.model.ThemeModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.context;

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

    private static Theme theme;
    private static Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public static Theme getInstance() {
        if (theme == null) {
            theme = new Theme();
        }
        return theme;
    }

    public static String default_appBarColor = "#45B321";
    public static String default_notificationColor = "#e05353";
    public static String default_toggleButtonColor = "#00B0BF";
    public static String default_attachmentColor = default_appBarColor;
    public static String default_headerTextColor = "#00B0BF";
    public static String default_progressColor = "#45B321";
    public static String default_dark_appBarColor = "#383838";
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

    private static Paint paint;

    public static void setThemeColor() {
        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        G.themeColor = preferences.getInt(SHP_SETTING.KEY_THEME_COLOR, DEFAULT);
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

    public static Drawable createSimpleSelectorRoundRectDrawable(int rad, int defaultColor, int pressedColor) {
        return createSimpleSelectorRoundRectDrawable(rad, defaultColor, pressedColor, pressedColor);
    }

    public static Drawable createSimpleSelectorRoundRectDrawable(int rad, int defaultColor, int pressedColor, int maskColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        ShapeDrawable pressedDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        pressedDrawable.getPaint().setColor(maskColor);
        if (Build.VERSION.SDK_INT >= 21) {
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{pressedColor}
            );
            return new RippleDrawable(colorStateList, defaultDrawable, pressedDrawable);
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, pressedDrawable);
            stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
            return stateListDrawable;
        }
    }

    public static Drawable getSelectorDrawable(int color) {
        return createSelectorDrawable(color, 2);
    }

    public static Drawable getSelectorDrawable() {
        return getSelectorDrawable(0x0f000000);
    }

    public static Drawable createSelectorDrawable(int color) {
        return createSelectorDrawable(color, 1, -1);
    }

    public static Drawable createSelectorDrawable(int color, int maskType) {
        return createSelectorDrawable(color, maskType, -1);
    }

    public static Drawable createSelectorDrawable(int color, int maskType, int radius) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable drawable = null;
            if ((maskType == 1 || maskType == 5) && Build.VERSION.SDK_INT >= 23) {
                drawable = null;
            } else if (maskType == 1 || maskType == 3 || maskType == 4 || maskType == 5 || maskType == 6 || maskType == 7) {
                maskPaint.setColor(0xffffffff);
                drawable = new Drawable() {
                    RectF rect;

                    @Override
                    public void draw(@NonNull Canvas canvas) {
                        Rect bounds = getBounds();
                        if (maskType == 7) {
                            if (rect == null) {
                                rect = new RectF();
                            }
                            rect.set(bounds);
                            canvas.drawRoundRect(rect, LayoutCreator.dp(6), LayoutCreator.dp(6), maskPaint);
                        } else {
                            int rad;
                            if (maskType == 1 || maskType == 6) {
                                rad = LayoutCreator.dp(20);
                            } else if (maskType == 3) {
                                rad = (Math.max(bounds.width(), bounds.height()) / 2);
                            } else {
                                rad = (int) Math.ceil(Math.sqrt((bounds.left - bounds.centerX()) * (bounds.left - bounds.centerX()) + (bounds.top - bounds.centerY()) * (bounds.top - bounds.centerY())));
                            }
                            canvas.drawCircle(bounds.centerX(), bounds.centerY(), rad, maskPaint);
                        }
                    }

                    @Override
                    public void setAlpha(int alpha) {

                    }

                    @Override
                    public void setColorFilter(ColorFilter colorFilter) {

                    }

                    @Override
                    public int getOpacity() {
                        return PixelFormat.UNKNOWN;
                    }
                };
            } else if (maskType == 2) {
                drawable = new ColorDrawable(0xffffffff);
            }
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{color}
            );
            RippleDrawable rippleDrawable = new RippleDrawable(colorStateList, null, drawable);
            if (Build.VERSION.SDK_INT >= 23) {
                if (maskType == 1) {
                    rippleDrawable.setRadius(radius <= 0 ? LayoutCreator.dp(20) : radius);
                } else if (maskType == 5) {
                    rippleDrawable.setRadius(RippleDrawable.RADIUS_AUTO);
                }
            }
            return rippleDrawable;
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(color));
            stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0x00000000));
            return stateListDrawable;
        }
    }

    public List<ThemeModel> getThemeList() {
        List<ThemeModel> themeModelList = new ArrayList<>();
        themeModelList.add(new ThemeModel(DEFAULT, R.string.default_theme_title));
        themeModelList.add(new ThemeModel(DARK, R.string.dark_theme_title));
        themeModelList.add(new ThemeModel(RED, R.string.red_theme_title));
        themeModelList.add(new ThemeModel(PINK, R.string.pink_theme_title));
        themeModelList.add(new ThemeModel(PURPLE, R.string.purple_theme_title));
        themeModelList.add(new ThemeModel(BLUE, R.string.blue_theme_title));
        themeModelList.add(new ThemeModel(GREEN, R.string.green_theme_title));
        themeModelList.add(new ThemeModel(AMBER, R.string.amber_theme_title));
        themeModelList.add(new ThemeModel(ORANGE, R.string.orange_theme_title));
        themeModelList.add(new ThemeModel(GREY, R.string.gray_theme_title));
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

    public int getUserProfileTabSelector(Context context) {
        return getDrawableAttr(context, R.attr.iGapProfileStroke);
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

    public int getCardViewColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapCardViewColor);
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

    public int getDarkAccentColor(Context context) {
        return getColorFromAttr(context, R.attr.colorAccentDark);
    }

    public int getPrimaryDarkColor(Context context) {
        return getColorFromAttr(context, R.attr.colorPrimaryDark);
    }

    public int getRootColor(Context context) {
        return getColorFromAttr(context, R.attr.rootBackgroundColor);
    }

    public int getButtonColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapButtonColor);
    }

    public int getButtonTextColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapButtonTextColor);
    }

    public int getForwardFromTextColor(Context context) {
        return getColorFromAttr(context, R.attr.colorAccent);
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

    public int getSendReplayUserColor(Context context) {
        return getColorFromAttr(context, R.attr.iGapSendReplayColor);
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

    public int getCardToCardButton(Context context) {
        return getDrawableAttr(context, R.attr.iGapCardToCardButton);
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


    public Paint getDividerPaint(Context context) {
        if (paint == null) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(getDividerColor(context));
        }
        return paint;
    }
}

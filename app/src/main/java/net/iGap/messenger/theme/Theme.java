package net.iGap.messenger.theme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.text.TextUtils;
import android.util.StateSet;

import androidx.core.graphics.drawable.DrawableCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.controllers.SharedManager;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperLog;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.NotificationCenter;
import net.iGap.messenger.ui.components.MsgClockDrawable;
import net.iGap.messenger.ui.components.StatusDrawable;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StatusBarUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import static net.iGap.module.AndroidUtils.parseInt;

public class Theme {

    public final static int THEME_TYPE_DAY = 0;
    public final static int THEME_TYPE_DARK = 1;
    public final static int THEME_TYPE_NIGHT = 2;

    public static class ThemeInfo {
        public String name;
        public String filePath;
        public String assetName;
        public int account;
        public boolean loaded = true;

        ThemeInfo() {
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return name;
        }

        static ThemeInfo createWithString(String string) {
            if (TextUtils.isEmpty(string)) {
                return null;
            }
            String[] args = string.split("\\|");
            if (args.length != 2) {
                return null;
            }
            ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.name = args[0];
            themeInfo.filePath = args[1];
            return themeInfo;
        }
    }

    public static final String DAY_GREEN_THEME = "DayGreen";
    public static final String DAY_RED_THEME = "DayRed";
    public static final String DAY_PINK_THEME = "DayPink";
    public static final String DAY_CYAN_THEME = "DayCyan";
    public static final String DAY_BLUE_THEME = "DayBlue";
    public static final String DAY_YELLOW_THEME = "DayYellow";
    public static final String DAY_ORANGE_THEME = "DayOrange";

    public static final String DARK_GREEN_THEME = "DarkGreen";
    public static final String DARK_RED_THEME = "DarkRed";
    public static final String DARK_PINK_THEME = "DarkPink";
    public static final String DARK_CYAN_THEME = "DarkCyan";
    public static final String DARK_BLUE_THEME = "DarkBlue";
    public static final String DARK_YELLOW_THEME = "DarkYellow";
    public static final String DARK_ORANGE_THEME = "DarkOrange";

    public static final String NIGHT_GREEN_THEME = "NightGreen";
    public static final String NIGHT_RED_THEME = "NightRed";
    public static final String NIGHT_PINK_THEME = "NightPink";
    public static final String NIGHT_CYAN_THEME = "NightCyan";
    public static final String NIGHT_BLUE_THEME = "NightBlue";
    public static final String NIGHT_YELLOW_THEME = "NightYellow";
    public static final String NIGHT_ORANGE_THEME = "NightOrange";

    private static Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public static ArrayList<ThemeInfo> themes;
    private static HashMap<String, ThemeInfo> themesDict;
    private static ThemeInfo currentTheme;
    private static ThemeInfo defaultTheme;
    private static int currentThemeType;

    public static Paint dividerPaint;

    public static final String key_window_background = "key_window_background";
    public static final String key_chat_background = "key_chat_background";
    public static final String key_popup_background = "key_popup_background";
    public static final String key_toolbar_background = "key_toolbar_background";
    public static final String key_default_text = "key_default_text";
    public static final String key_title_text = "key_title_text";
    public static final String key_subtitle_text = "key_subtitle_text";
    public static final String key_link_text = "key_link_text";
    public static final String key_button_background = "key_button_background";
    public static final String key_button_text = "key_button_text";
    public static final String key_icon = "key_icon";
    public static final String key_white = "key_white";
    public static final String key_black = "key_black";
    public static final String key_theme_color = "key_theme_color";
    public static final String key_light_theme_color = "key_light_theme_color";
    public static final String key_opacity_theme_color = "key_opacity_theme_color";
    public static final String key_dark_theme_color = "key_dark_theme_color";
    public static final String key_gray = "key_gray";
    public static final String key_light_gray = "key_light_gray";
    public static final String key_dark_gray = "key_dark_gray";
    public static final String key_red = "key_red";
    public static final String key_light_red = "key_light_red ";
    public static final String key_dark_red = "key_dark_red";
    public static final String key_gray_background = "key_gray_background";
    public static final String key_gray_background_text = "key_gray_background_text";
    public static final String key_background_footer = "key_background_footer";
    public static final String key_footer_gray_icon = "key_footer_gray_icon";
    public static final String key_footer_theme_icon = "key_footer_theme_icon";
    public static final String key_line = "key_line";
    public static final String key_chat_item_holder = "key_chat_item_holder";
    public static final String key_send_item_background = "key_send_item_background";
    public static final String key_received_item_background = "key_received_item_background";

    private static HashMap<String, Integer> defaultColors = new HashMap<>();
    private static HashMap<String, String> fallbackKeys = new HashMap<>();
    private static HashMap<String, Integer> currentColorsNoAccent;
    private static HashMap<String, Integer> currentColors;

    static {
        defaultColors.put(key_window_background, 0xFFFFFFFF);
        defaultColors.put(key_chat_background, 0xFFF4F8F2);
        defaultColors.put(key_popup_background, 0xFFF9F8F8);
        defaultColors.put(key_toolbar_background, 0xFF32AF0C);
        defaultColors.put(key_default_text, 0xFF110F15);
        defaultColors.put(key_title_text, 0xFF88B884);
        defaultColors.put(key_subtitle_text, 0xFF959595);
        defaultColors.put(key_link_text, 0xFF29B327);
        defaultColors.put(key_button_background, 0xFF32AF0C);
        defaultColors.put(key_button_text, 0xFFFFFFFF);
        defaultColors.put(key_icon, 0xFF646464);
        defaultColors.put(key_white, 0xFFFFFFFF);
        defaultColors.put(key_black, 0xFF000000);
        defaultColors.put(key_theme_color, 0xFF32AF0C);
        defaultColors.put(key_light_theme_color, 0xFFB3E2A5);
        defaultColors.put(key_opacity_theme_color, 0x60B3E2A5);
        defaultColors.put(key_dark_theme_color, 0xFF2C990B);
        defaultColors.put(key_gray, 0xFFE1E1E1);
        defaultColors.put(key_light_gray, 0xFFF9F8F8);
        defaultColors.put(key_dark_gray, 0xFF646464);
        defaultColors.put(key_red, 0xFFF30000);
        defaultColors.put(key_light_red, 0xFFFF4343);
        defaultColors.put(key_dark_red, 0xFFA30000);
        defaultColors.put(key_gray_background, 0xFFC0C0C0);
        defaultColors.put(key_gray_background_text, 0xFFFFFFFF);
        defaultColors.put(key_background_footer, 0xFFF2F2F2);
        defaultColors.put(key_footer_gray_icon, 0xFF959595);
        defaultColors.put(key_footer_theme_icon, 0xFF29B327);
        defaultColors.put(key_line, 0xFFEBEBEB);
        defaultColors.put(key_chat_item_holder, 0xFFA0B79A);
        defaultColors.put(key_send_item_background, 0xFFFFFFFF);
        defaultColors.put(key_received_item_background, 0xFFEAF7E6);

        themes = new ArrayList<>();
        themesDict = new HashMap<>();
        currentColorsNoAccent = new HashMap<>();
        currentColors = new HashMap<>();

        //THEME_TYPE_DAY
        ThemeInfo themeInfo = new ThemeInfo();
        themeInfo.name = DAY_GREEN_THEME;
        themeInfo.assetName = "day_green.attheme";
        themes.add(defaultTheme = themeInfo);
        themesDict.put(DAY_GREEN_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DAY_YELLOW_THEME;
        themeInfo.assetName = "day_yellow.attheme";
        themes.add(themeInfo);
        themesDict.put(DAY_YELLOW_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DAY_RED_THEME;
        themeInfo.assetName = "day_red.attheme";
        themes.add(themeInfo);
        themesDict.put(DAY_RED_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DAY_PINK_THEME;
        themeInfo.assetName = "day_pink.attheme";
        themes.add(themeInfo);
        themesDict.put(DAY_PINK_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DAY_CYAN_THEME;
        themeInfo.assetName = "day_cyan.attheme";
        themes.add(themeInfo);
        themesDict.put(DAY_CYAN_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DAY_BLUE_THEME;
        themeInfo.assetName = "day_blue.attheme";
        themes.add(themeInfo);
        themesDict.put(DAY_BLUE_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DAY_ORANGE_THEME;
        themeInfo.assetName = "day_orange.attheme";
        themes.add(themeInfo);
        themesDict.put(DAY_ORANGE_THEME, themeInfo);

        //THEME_TYPE_DARK
        themeInfo = new ThemeInfo();
        themeInfo.name = DARK_GREEN_THEME;
        themeInfo.assetName = "dark_green.attheme";
        themes.add(themeInfo);
        themesDict.put(DARK_GREEN_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DARK_YELLOW_THEME;
        themeInfo.assetName = "dark_yellow.attheme";
        themes.add(themeInfo);
        themesDict.put(DARK_YELLOW_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DARK_RED_THEME;
        themeInfo.assetName = "dark_red.attheme";
        themes.add(themeInfo);
        themesDict.put(DARK_RED_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DARK_PINK_THEME;
        themeInfo.assetName = "dark_pink.attheme";
        themes.add(themeInfo);
        themesDict.put(DARK_PINK_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DARK_CYAN_THEME;
        themeInfo.assetName = "dark_cyan.attheme";
        themes.add(themeInfo);
        themesDict.put(DARK_CYAN_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DARK_BLUE_THEME;
        themeInfo.assetName = "dark_blue.attheme";
        themes.add(themeInfo);
        themesDict.put(DARK_BLUE_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = DARK_ORANGE_THEME;
        themeInfo.assetName = "dark_orange.attheme";
        themes.add(themeInfo);
        themesDict.put(DARK_ORANGE_THEME, themeInfo);

        //THEME_TYPE_NIGHT
        themeInfo = new ThemeInfo();
        themeInfo.name = NIGHT_GREEN_THEME;
        themeInfo.assetName = "night_green.attheme";
        themes.add(themeInfo);
        themesDict.put(NIGHT_GREEN_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = NIGHT_YELLOW_THEME;
        themeInfo.assetName = "night_yellow.attheme";
        themes.add(themeInfo);
        themesDict.put(NIGHT_YELLOW_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = NIGHT_RED_THEME;
        themeInfo.assetName = "night_red.attheme";
        themes.add(themeInfo);
        themesDict.put(NIGHT_RED_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = NIGHT_PINK_THEME;
        themeInfo.assetName = "night_pink.attheme";
        themes.add(themeInfo);
        themesDict.put(NIGHT_PINK_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = NIGHT_CYAN_THEME;
        themeInfo.assetName = "night_cyan.attheme";
        themes.add(themeInfo);
        themesDict.put(NIGHT_CYAN_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = NIGHT_BLUE_THEME;
        themeInfo.assetName = "night_blue.attheme";
        themes.add(themeInfo);
        themesDict.put(NIGHT_BLUE_THEME, themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = NIGHT_ORANGE_THEME;
        themeInfo.assetName = "night_orange.attheme";
        themes.add(themeInfo);
        themesDict.put(NIGHT_ORANGE_THEME, themeInfo);

        applyTheme(getCurrentTheme(), getCurrentThemeType());
    }

    private static Method StateListDrawable_getStateDrawableMethod;

    public static boolean isDark() {
        return getCurrentThemeType() == THEME_TYPE_DARK;
    }

    public static boolean isDay() {
        return getCurrentThemeType() == THEME_TYPE_DAY;
    }

    public static boolean isNight() {
        return getCurrentThemeType() == THEME_TYPE_NIGHT;
    }

    @SuppressLint("PrivateApi")
    private static Drawable getStateDrawable(Drawable drawable, int index) {
        if (Build.VERSION.SDK_INT >= 29 && drawable instanceof StateListDrawable) {
            return ((StateListDrawable) drawable).getStateDrawable(index);
        } else {
            if (StateListDrawable_getStateDrawableMethod == null) {
                try {
                    StateListDrawable_getStateDrawableMethod = StateListDrawable.class.getDeclaredMethod("getStateDrawable", int.class);
                } catch (Throwable ignore) {

                }
            }
            if (StateListDrawable_getStateDrawableMethod == null) {
                return null;
            }
            try {
                return (Drawable) StateListDrawable_getStateDrawableMethod.invoke(drawable, index);
            } catch (Exception ignore) {

            }
            return null;
        }
    }

    public static Drawable createEditTextDrawable(Context context, boolean alert) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(R.drawable.search_dark).mutate();
        defaultDrawable.setColorFilter(new PorterDuffColorFilter(getColor(key_light_gray), PorterDuff.Mode.MULTIPLY));
        Drawable pressedDrawable = resources.getDrawable(R.drawable.search_dark_activated).mutate();
        pressedDrawable.setColorFilter(new PorterDuffColorFilter(getColor(key_title_text), PorterDuff.Mode.MULTIPLY));
        StateListDrawable stateListDrawable = new StateListDrawable() {
            @Override
            public boolean selectDrawable(int index) {
                if (Build.VERSION.SDK_INT < 21) {
                    Drawable drawable = Theme.getStateDrawable(this, index);
                    ColorFilter colorFilter = null;
                    if (drawable instanceof BitmapDrawable) {
                        colorFilter = ((BitmapDrawable) drawable).getPaint().getColorFilter();
                    } else if (drawable instanceof NinePatchDrawable) {
                        colorFilter = ((NinePatchDrawable) drawable).getPaint().getColorFilter();
                    }
                    boolean result = super.selectDrawable(index);
                    if (colorFilter != null) {
                        drawable.setColorFilter(colorFilter);
                    }
                    return result;
                }
                return super.selectDrawable(index);
            }
        };
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, pressedDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

    public static Drawable createRoundRectDrawable(int rad, int defaultColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        return defaultDrawable;
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

    public static Drawable getRoundRectSelectorDrawable(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = createRoundRectDrawable(LayoutCreator.dp(3), 0xffffffff);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{(color & 0x00ffffff) | 0x19000000}
            );
            return new RippleDrawable(colorStateList, null, maskDrawable);
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, createRoundRectDrawable(LayoutCreator.dp(3), (color & 0x00ffffff) | 0x19000000));
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, createRoundRectDrawable(LayoutCreator.dp(3), (color & 0x00ffffff) | 0x19000000));
            stateListDrawable.addState(StateSet.WILD_CARD, new ColorDrawable(0x00000000));
            return stateListDrawable;
        }
    }

    public static Drawable createSelectorDrawable(int color) {
        return createSelectorDrawable(color, 1, -1);
    }

    public static Drawable createSelectorDrawable(int color, int maskType) {
        return createSelectorDrawable(color, maskType, -1);
    }

    public static Drawable createSelectorDrawable(int color, int maskType, int radius) {
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable maskDrawable = null;
            if ((maskType == 1 || maskType == 5) && Build.VERSION.SDK_INT >= 23) {
                maskDrawable = null;
            } else if (maskType == 1 || maskType == 3 || maskType == 4 || maskType == 5 || maskType == 6 || maskType == 7) {
                maskPaint.setColor(0xffffffff);
                maskDrawable = new Drawable() {

                    RectF rect;

                    @Override
                    public void draw(Canvas canvas) {
                        android.graphics.Rect bounds = getBounds();
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
                maskDrawable = new ColorDrawable(0xffffffff);
            }
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{color}
            );
            RippleDrawable rippleDrawable = new RippleDrawable(colorStateList, null, maskDrawable);
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

    public static Drawable tintDrawable(Drawable drawable, Context context, int colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(colors));
        return wrappedDrawable;
    }

    public static Drawable getSelectorDrawable(int color) {
        return createSelectorDrawable(color, 2);
    }

    public static Drawable createSimpleSelectorCircleDrawable(int size, int defaultColor, int pressedColor) {
        OvalShape ovalShape = new OvalShape();
        ovalShape.resize(size, size);
        ShapeDrawable defaultDrawable = new ShapeDrawable(ovalShape);
        defaultDrawable.getPaint().setColor(defaultColor);
        ShapeDrawable pressedDrawable = new ShapeDrawable(ovalShape);
        if (Build.VERSION.SDK_INT >= 21) {
            pressedDrawable.getPaint().setColor(0xffffffff);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{StateSet.WILD_CARD},
                    new int[]{pressedColor}
            );
            return new RippleDrawable(colorStateList, defaultDrawable, pressedDrawable);
        } else {
            pressedDrawable.getPaint().setColor(pressedColor);
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
            stateListDrawable.addState(new int[]{android.R.attr.state_focused}, pressedDrawable);
            stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
            return stateListDrawable;
        }
    }

    public static ThemeInfo getTheme(String key) {
        return themesDict.get(key);
    }

    public static void applyTheme(ThemeInfo themeInfo, int themeType) {
        if (themeInfo == null) {
            return;
        }
        setCurrentTheme(themeInfo);
        setCurrentThemeType(themeType);
        currentColorsNoAccent.clear();
        currentColorsNoAccent = getThemeFileValues(themeInfo.assetName);
        if (G.currentActivity != null) {
            G.currentActivity.getTheme().applyStyle(Theme.themeStyle(), true);
        }
        refreshThemeColors();
    }

    public static void refreshThemeColors() {
        currentColors.clear();
        currentColors.putAll(currentColorsNoAccent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (G.currentActivity != null) {
                StatusBarUtil.setColor(G.currentActivity, Theme.getColor(Theme.key_dark_theme_color), 50);
            }
        }
        if (dividerPaint != null) {
            dividerPaint.setColor(getColor(key_line));
        }
        G.runOnUiThread(() -> NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme));
    }

    public static ThemeInfo getCurrentTheme() {
        SharedPreferences preferences = SharedManager.getInstance().getSettingSharedPreferences();
        if (preferences != null) {
            String theme = preferences.getString(SHP_SETTING.KEY_THEME, null);
            if (theme == null) {
                return defaultTheme;
            } else {
                return themesDict.get(theme);
            }
        }
        return defaultTheme;
    }

    public static void setCurrentTheme(ThemeInfo currentTheme) {
        SharedPreferences preferences = SharedManager.getInstance().getSettingSharedPreferences();
        if (preferences != null) {
            preferences.edit().putString(SHP_SETTING.KEY_THEME, currentTheme.getKey()).apply();
            Theme.currentTheme = currentTheme;
            G.themeColor = currentTheme.getKey();
        }
    }

    public static int getCurrentThemeType() {
        SharedPreferences preferences = SharedManager.getInstance().getSettingSharedPreferences();
        if (preferences != null) {
            currentThemeType = preferences.getInt(SHP_SETTING.KEY_THEME_TYPE, 0);
            return currentThemeType;
        }
        return 0;
    }

    public static void setCurrentThemeType(int currentThemeType) {
        SharedPreferences preferences = SharedManager.getInstance().getSettingSharedPreferences();
        if (preferences != null) {
            preferences.edit().putInt(SHP_SETTING.KEY_THEME_TYPE, currentThemeType).apply();
            Theme.currentThemeType = currentThemeType;
        }
    }

    public static File getAssetFile(String assetName) {
        if (G.context != null){
            File themeDir = G.context.getFilesDir();
            try {
                if (!themeDir.exists()) {
                    themeDir.mkdirs();
                }
            } catch (Exception e) {
                HelperLog.getInstance().setErrorLog(e);
                return null;
            }
            File file = new File(G.context.getApplicationInfo().dataDir, assetName);
            long size;
            try {
                InputStream stream = G.context.getAssets().open(assetName);
                size = stream.available();
                stream.close();
            } catch (Exception e) {
                size = 0;
                HelperLog.getInstance().setErrorLog(e);
            }
            if (!file.exists() || size != 0) {
                try (InputStream in = G.context.getAssets().open(assetName)) {
                    copyFile(in, file);
                } catch (Exception e) {
                    HelperLog.getInstance().setErrorLog(e);
                }
            }
            return file;
        } else {
            return null;
        }
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        OutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        int len;
        while ((len = sourceFile.read(buf)) > 0) {
            Thread.yield();
            out.write(buf, 0, len);
        }
        out.close();
        sourceFile.close();
        return true;
    }

    public static HashMap<String, Integer> getThemeFileValues(String assetName) {
        FileInputStream stream = null;
        File file;
        HashMap<String, Integer> stringMap = new HashMap<>();
        try {
            byte[] bytes = new byte[1024];
            int currentPosition = 0;
            if (assetName != null && getAssetFile(assetName) != null) {
                file = getAssetFile(assetName);
                stream = new FileInputStream(file);
            }
            int idx;
            int read;
            boolean finished = false;
            while ((read = stream.read(bytes)) != -1) {
                int previousPosition = currentPosition;
                int start = 0;
                for (int a = 0; a < read; a++) {
                    if (bytes[a] == '\n') {
                        int len = a - start + 1;
                        String line = new String(bytes, start, len - 1);
                        if (line.startsWith("WPS")) {
                            finished = true;
                            break;
                        } else {
                            if ((idx = line.indexOf('=')) != -1) {
                                String key = line.substring(0, idx);
                                String param = line.substring(idx + 1);
                                int value;
                                if (param.length() > 0 && param.charAt(0) == '#') {
                                    try {
                                        if (param.endsWith("\r")) {
                                            param = param.replace("\r","");
                                        }
                                        value = Color.parseColor(param);
                                    } catch (Exception ignore) {
                                        value = parseInt(param);
                                    }
                                } else {
                                    value = parseInt(param);
                                }
                                stringMap.put(key, value);
                            }
                        }
                        start += len;
                        currentPosition += len;
                    }
                }
                if (previousPosition == currentPosition) {
                    break;
                }
                stream.getChannel().position(currentPosition);
                if (finished) {
                    break;
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return stringMap;
    }

    public static Drawable getThemedDrawable(Context context, int resId, String key) {
        return getThemedDrawable(context, resId, getColor(key));
    }

    public static Drawable getThemedDrawable(Context context, int resId, int color) {
        if (context == null) {
            return null;
        }
        Drawable drawable = context.getResources().getDrawable(resId).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        return drawable;
    }

    static int getDefaultColor(String key) {
        Integer value = defaultColors.get(key);
        return value == null ? 0xffff0000 : value;
    }

    public static int getColor(String key) {
        if (currentTheme == defaultTheme) {
            return getDefaultColor(key);
        }
        Integer color = currentColors.get(key);
        if (color == null) {
            String fallbackKey = fallbackKeys.get(key);
            if (fallbackKey != null) {
                color = currentColors.get(fallbackKey);
            }
            if (color == null) {
                return getDefaultColor(key);
            }
        }
        return color;
    }

    public static void setDrawableColor(Drawable drawable, int color) {
        if (drawable == null) {
            return;
        }
        if (drawable instanceof StatusDrawable) {
            ((StatusDrawable) drawable).setColor(color);
        } else if (drawable instanceof MsgClockDrawable) {
            ((MsgClockDrawable) drawable).setColor(color);
        } else if (drawable instanceof ShapeDrawable) {
            ((ShapeDrawable) drawable).getPaint().setColor(color);
        } else {
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }

    public static void setSelectorDrawableColor(Drawable drawable, int color, boolean selected) {
        if (drawable instanceof StateListDrawable) {
            try {
                Drawable state;
                if (selected) {
                    state = getStateDrawable(drawable, 0);
                    if (state instanceof ShapeDrawable) {
                        ((ShapeDrawable) state).getPaint().setColor(color);
                    } else {
                        state.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                    state = getStateDrawable(drawable, 1);
                } else {
                    state = getStateDrawable(drawable, 2);
                }
                if (state instanceof ShapeDrawable) {
                    ((ShapeDrawable) state).getPaint().setColor(color);
                } else {
                    state.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            } catch (Throwable ignore) {

            }
        } else if (Build.VERSION.SDK_INT >= 21 && drawable instanceof RippleDrawable) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            if (selected) {
                rippleDrawable.setColor(new ColorStateList(
                        new int[][]{StateSet.WILD_CARD},
                        new int[]{color}
                ));
            } else {
                if (rippleDrawable.getNumberOfLayers() > 0) {
                    Drawable drawable1 = rippleDrawable.getDrawable(0);
                    if (drawable1 instanceof ShapeDrawable) {
                        ((ShapeDrawable) drawable1).getPaint().setColor(color);
                    } else {
                        drawable1.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                }
            }
        }
    }

    public static Paint getDividerPaint() {
        if (dividerPaint == null) {
            dividerPaint = new Paint();
            dividerPaint.setStrokeWidth(1);
            dividerPaint.setColor(getColor(key_line));
        }
        return dividerPaint;
    }

    public static int findCurrentAccent() {
        int green = 0;
        int yellow = 1;
        int red = 2;
        int pink = 3;
        int cyan = 4;
        int blue = 5;
        int orange = 6;
        switch (Theme.getCurrentTheme().getName()) {
            case DAY_RED_THEME:
            case DARK_RED_THEME:
            case NIGHT_RED_THEME:
                return red;
            case DAY_PINK_THEME:
            case DARK_PINK_THEME:
            case NIGHT_PINK_THEME:
                return pink;
            case DAY_CYAN_THEME:
            case DARK_CYAN_THEME:
            case NIGHT_CYAN_THEME:
                return cyan;
            case DAY_BLUE_THEME:
            case DARK_BLUE_THEME:
            case NIGHT_BLUE_THEME:
                return blue;
            case DAY_YELLOW_THEME:
            case DARK_YELLOW_THEME:
            case NIGHT_YELLOW_THEME:
                return yellow;
            case DAY_ORANGE_THEME:
            case DARK_ORANGE_THEME:
            case NIGHT_ORANGE_THEME:
                return orange;
            default:
                return green;
        }
    }

    public static int themeStyle() {
        switch (Theme.getCurrentTheme().getName()) {
            case DAY_RED_THEME:
                return R.style.DayRedTheme;
            case DARK_RED_THEME:
                return R.style.DarkRedTheme;
            case NIGHT_RED_THEME:
                return R.style.NightRedTheme;
            case DAY_PINK_THEME:
                return R.style.DayPinkTheme;
            case DARK_PINK_THEME:
                return R.style.DarkPinkTheme;
            case NIGHT_PINK_THEME:
                return R.style.NightPinkTheme;
            case DAY_CYAN_THEME:
                return R.style.DayCyanTheme;
            case DARK_CYAN_THEME:
                return R.style.DarkCyanTheme;
            case NIGHT_CYAN_THEME:
                return R.style.NightCyanTheme;
            case DAY_BLUE_THEME:
                return R.style.DayBlueTheme;
            case DARK_BLUE_THEME:
                return R.style.DarkBlueTheme;
            case NIGHT_BLUE_THEME:
                return R.style.NightBlueTheme;
            case DAY_YELLOW_THEME:
                return R.style.DayYellowTheme;
            case DARK_YELLOW_THEME:
                return R.style.DarkYellowTheme;
            case NIGHT_YELLOW_THEME:
                return R.style.NightYellowTheme;
            case DAY_ORANGE_THEME:
                return R.style.DayOrangeTheme;
            case DARK_ORANGE_THEME:
                return R.style.DarkOrangeTheme;
            case NIGHT_ORANGE_THEME:
                return R.style.NightOrangeTheme;
            default:
                return R.style.BaseTheme;
        }
    }

    public static void setThemeAccordingToConfiguration(Configuration config ){
        SharedPreferences preferences = SharedManager.getInstance().getSettingSharedPreferences();
        boolean systemDarkMode = false;
        if (preferences != null) {
            systemDarkMode = preferences.getBoolean(SHP_SETTING.KEY_SYSTEM_DARK_MODE, false);
        }
        int colors = 7;
        if (config.uiMode == 33 && systemDarkMode){
            Theme.setCurrentThemeType(THEME_TYPE_NIGHT);
        } /*else if (config.uiMode == 17 && systemDarkMode){
            Theme.setCurrentThemeType(THEME_TYPE_DAY);
        }*/
        Theme.ThemeInfo themeInfo = Theme.themes.get((colors * Theme.getCurrentThemeType()) + Theme.findCurrentAccent());
        Theme.setCurrentTheme(themeInfo);
        Theme.applyTheme(Theme.getCurrentTheme(),Theme.getCurrentThemeType());
        if (G.currentActivity != null) {
            G.currentActivity.getTheme().applyStyle(Theme.themeStyle(), true);
        }
    }
}

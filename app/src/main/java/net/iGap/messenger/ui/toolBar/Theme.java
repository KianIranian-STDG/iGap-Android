package net.iGap.messenger.ui.toolBar;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.StateSet;

import net.iGap.helper.LayoutCreator;
import java.util.HashMap;

public class Theme {

    private static HashMap<String, Integer> defaultColors = new HashMap<>();
    private static Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    public static final String key_dialogButton = "dialogButton";
    public static final String key_dialogButtonSelector = "dialogButtonSelector";
    public static final String key_dialogIcon = "dialogIcon";
    public static final String key_dialogTextBlack = "dialogTextBlack";
    public static final String key_dialogBackground = "dialogBackground";
    public static final String key_dialogTextGray3 = "dialogTextGray3";
    public static final String key_dialogScrollGlow = "dialogScrollGlow";
    public static final String key_dialogTextLink = "dialogTextLink";
    public static final String key_dialogProgressCircle = "dialogProgressCircle";
    public static final String key_dialogLineProgress = "dialogLineProgress";
    public static final String key_dialogLineProgressBackground = "dialogLineProgressBackground";
    public static final String key_dialogTextGray2 = "dialogTextGray2";
    public static final String key_dialog_inlineProgressBackground = "dialog_inlineProgressBackground";
    public static final String key_dialog_inlineProgress = "dialog_inlineProgress";

    static {
        defaultColors.put(key_dialogBackground, 0xffffffff);
        defaultColors.put(key_dialogTextBlack, 0xff222222);
        defaultColors.put(key_dialogTextLink, 0xff2678b6);
        defaultColors.put(key_dialogTextGray2, 0xff757575);
        defaultColors.put(key_dialogTextGray3, 0xff999999);
        defaultColors.put(key_dialogIcon, 0xff676b70);
        defaultColors.put(key_dialogProgressCircle, 0xff289deb);
        defaultColors.put(key_dialogLineProgress, 0xff527da3);
        defaultColors.put(key_dialogLineProgressBackground, 0xffdbdbdb);
        defaultColors.put(key_dialogButton, 0xff4991cc);
        defaultColors.put(key_dialogButtonSelector, 0x0f000000);
        defaultColors.put(key_dialogScrollGlow, 0xfff5f6f7);
        defaultColors.put(key_dialog_inlineProgressBackground, 0xf6f0f2f5);
        defaultColors.put(key_dialog_inlineProgress, 0xff6b7378);
    }

    public static Drawable createSelectorDrawable(int color, int maskType) {
        return createSelectorDrawable(color, maskType, -1);
    }
    
    public static Drawable createSelectorDrawable(int color, int maskType, int radius) {
        Drawable drawable;
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

    public static int getDefaultColor(String key) {
        Integer value = defaultColors.get(key);
        if (value == null) {
            return 0xffff0000;
        }
        return value;
    }

    public static Drawable createRoundRectDrawable(int rad, int defaultColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        return defaultDrawable;
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
}

package net.iGap.messenger.theme;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;
import net.iGap.messenger.dialog.LineProgressView;
import net.iGap.messenger.ui.cell.AccountCell;
import net.iGap.messenger.ui.cell.CallLogCell;
import net.iGap.messenger.ui.cell.EditCell;
import net.iGap.messenger.ui.cell.EditTextCell;
import net.iGap.messenger.ui.cell.EmptyCell;
import net.iGap.messenger.ui.cell.FileCell;
import net.iGap.messenger.ui.cell.FolderRoomCell;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.IconCell;
import net.iGap.messenger.ui.cell.LanguageCell;
import net.iGap.messenger.ui.cell.ManageChatTextCell;
import net.iGap.messenger.ui.cell.ManageChatUserCell;
import net.iGap.messenger.ui.cell.MusicCell;
import net.iGap.messenger.ui.cell.NearByTitleCell;
import net.iGap.messenger.ui.cell.NearByUserCell;
import net.iGap.messenger.ui.cell.NotificationLedCell;
import net.iGap.messenger.ui.cell.SearchRoomCell;
import net.iGap.messenger.ui.cell.SelectRoomCell;
import net.iGap.messenger.ui.cell.SessionCell;
import net.iGap.messenger.ui.cell.ShadowSectionCell;
import net.iGap.messenger.ui.cell.SimpleTextView;
import net.iGap.messenger.ui.cell.TextCell;
import net.iGap.messenger.ui.cell.TextCheckCell;
import net.iGap.messenger.ui.cell.TextDetailCell;
import net.iGap.messenger.ui.cell.TextDetailSettingsCell;
import net.iGap.messenger.ui.cell.TextInfoPrivacyCell;
import net.iGap.messenger.ui.cell.TextSettingsCell;
import net.iGap.messenger.ui.cell.TextSizeCell;
import net.iGap.messenger.ui.cell.ThemePreviewMessagesCell;
import net.iGap.messenger.ui.cell.ThemesHorizontalListCell;
import net.iGap.messenger.ui.cell.UnRegisteredContactCell;
import net.iGap.messenger.ui.cell.VoiceCell;
import net.iGap.messenger.ui.components.CombinedDrawable;
import net.iGap.messenger.ui.components.EditTextBoldCursor;
import net.iGap.messenger.ui.components.EmptyTextProgressView;
import net.iGap.messenger.ui.components.SeekBarView;
import net.iGap.messenger.ui.toolBar.NumberTextView;
import net.iGap.module.customView.RadialProgressView;
import net.iGap.module.customView.RecyclerListView;

import java.lang.reflect.Field;

public class ThemeDescriptor {

    public static int FLAG_BACKGROUND                   = 0x00000001;
    public static int FLAG_LINKCOLOR                    = 0x00000002;
    public static int FLAG_TEXTCOLOR                    = 0x00000004;
    public static int FLAG_IMAGECOLOR                   = 0x00000008;
    public static int FLAG_CELLBACKGROUNDCOLOR          = 0x00000010;
    public static int FLAG_BACKGROUNDFILTER             = 0x00000020;
    public static int FLAG_AB_ITEMSCOLOR                = 0x00000040;
    public static int FLAG_AB_TITLECOLOR                = 0x00000080;
    public static int FLAG_AB_SELECTORCOLOR             = 0x00000100;
    public static int FLAG_AB_AM_ITEMSCOLOR             = 0x00000200;
    public static int FLAG_AB_SUBTITLECOLOR             = 0x00000400;
    public static int FLAG_PROGRESSBAR                  = 0x00000800;
    public static int FLAG_SELECTOR                     = 0x00001000;
    public static int FLAG_CHECKBOX                     = 0x00002000;
    public static int FLAG_CHECKBOXCHECK                = 0x00004000;
    public static int FLAG_LISTGLOWCOLOR                = 0x00008000;
    public static int FLAG_DRAWABLESELECTEDSTATE        = 0x00010000;
    public static int FLAG_USEBACKGROUNDDRAWABLE        = 0x00020000;
    public static int FLAG_CHECKTAG                     = 0x00040000;
    public static int FLAG_SECTIONS                     = 0x00080000;
    public static int FLAG_AB_AM_BACKGROUND             = 0x00100000;
    public static int FLAG_AB_AM_TOPBACKGROUND          = 0x00200000;
    public static int FLAG_AB_AM_SELECTORCOLOR          = 0x00400000;
    public static int FLAG_HINTTEXTCOLOR                = 0x00800000;
    public static int FLAG_CURSORCOLOR                  = 0x01000000;
    public static int FLAG_FASTSCROLL                   = 0x02000000;
    public static int FLAG_AB_SEARCHPLACEHOLDER         = 0x04000000;
    public static int FLAG_AB_SEARCH                    = 0x08000000;
    public static int FLAG_SELECTORWHITE                = 0x10000000;
    public static int FLAG_SERVICEBACKGROUND            = 0x20000000;
    public static int FLAG_AB_SUBMENUITEM               = 0x40000000;
    public static int FLAG_AB_SUBMENUBACKGROUND         = 0x80000000;

    private final View viewToInvalidate;
    private final String currentKey;
    private final ThemeDescriptorDelegate delegate;
    private final int currentFlag;

    public interface ThemeDescriptorDelegate {
        void didSetColor();
    }

    public ThemeDescriptor(View view, int flag,String key,ThemeDescriptorDelegate themeDescriptorDelegate) {
        currentKey = key;
        viewToInvalidate = view;
        currentFlag = flag;
        delegate = themeDescriptorDelegate;
    }

    public ThemeDescriptor(View view, int flag,String key) {
        currentKey = key;
        viewToInvalidate = view;
        currentFlag = flag;
        delegate = null;
    }

    public void setColor() {
        if (viewToInvalidate instanceof RecyclerListView) {
            RecyclerListView recyclerListView = (RecyclerListView) viewToInvalidate;
            recyclerListView.getRecycledViewPool().clear();
            int count = recyclerListView.getHiddenChildCount();
            for (int a = 0; a < count; a++) {
                processViewColor(recyclerListView.getHiddenChildAt(a));
            }
            count = recyclerListView.getCachedChildCount();
            for (int a = 0; a < count; a++) {
                processViewColor(recyclerListView.getCachedChildAt(a));
            }
            count = recyclerListView.getAttachedScrapChildCount();
            for (int a = 0; a < count; a++) {
                processViewColor(recyclerListView.getAttachedScrapChildAt(a));
            }
            for (int i = 0; i < recyclerListView.getChildCount(); i++) {
                processViewColor(recyclerListView.getChildAt(i));
            }
        } else {
            processViewColor(viewToInvalidate);
        }
    }

    private boolean checkTag(String key, View view) {
        if (key == null || view == null) {
            return false;
        }
        Object viewTag = view.getTag();
        if (viewTag instanceof String) {
            return ((String) viewTag).contains(key);
        }
        return false;
    }

    public int getSetColor() {
        return Theme.getColor(currentKey);
    }

    public String getTitle() {
        return currentKey;
    }

    public static void setScrollViewEdgeEffectColor(ScrollView scrollView, int color) {
        if (Build.VERSION.SDK_INT >= 29) {
            scrollView.setTopEdgeEffectColor(color);
            scrollView.setBottomEdgeEffectColor(color);
        } else if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = ScrollView.class.getDeclaredField("mEdgeGlowTop");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }

                field = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception ignore) {

            }
        }
    }

    public static void setViewPagerEdgeEffectColor(ViewPager viewPager, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = ViewPager.class.getDeclaredField("mLeftEdge");
                field.setAccessible(true);
                EdgeEffect mLeftEdge = (EdgeEffect) field.get(viewPager);
                if (mLeftEdge != null) {
                    mLeftEdge.setColor(color);
                }

                field = ViewPager.class.getDeclaredField("mRightEdge");
                field.setAccessible(true);
                EdgeEffect mRightEdge = (EdgeEffect) field.get(viewPager);
                if (mRightEdge != null) {
                    mRightEdge.setColor(color);
                }
            } catch (Exception ignore) {

            }
        }
    }

    private void processViewColor(View view) {
        if (view != null){
            if ((currentFlag & FLAG_TEXTCOLOR) != 0) {
                if (view instanceof TextCell) {
                    ((TextCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof AccountCell) {
                    ((AccountCell) view).setTextColor(Theme.getColor(currentKey),Theme.getColor(Theme.key_title_text));
                } else if (view instanceof CallLogCell) {
                    ((CallLogCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof HeaderCell){
                    ((HeaderCell) view).setTextColor(Theme.getColor(Theme.key_title_text));
                } else if (view instanceof EditCell) {
                    ((EditCell) view).setTextColor(Theme.getColor(currentKey),Theme.getColor(currentKey),Theme.getColor(currentKey));
                } else if (view instanceof EditTextCell) {
                    ((EditTextCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof FileCell) {
                    ((FileCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof FolderRoomCell) {
                    ((FolderRoomCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof LanguageCell) {
                    ((LanguageCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof MusicCell) {
                    ((MusicCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof ManageChatTextCell) {
                    ((ManageChatTextCell) view).setTextColor(Theme.getColor(Theme.key_title_text));
                } else if (view instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof NearByTitleCell) {
                    ((NearByTitleCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof NearByUserCell) {
                    ((NearByUserCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof NotificationLedCell) {
                    ((NotificationLedCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof SearchRoomCell) {
                    ((SearchRoomCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof SelectRoomCell) {
                    ((SelectRoomCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof SessionCell) {
                    ((SessionCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof SimpleTextView) {
                    ((SimpleTextView) view).setTextColor(Theme.getColor(currentKey));
                    ((SimpleTextView) view).setLinkTextColor(Theme.getColor(currentKey));
                } else if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof TextDetailCell) {
                    ((TextDetailCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof TextDetailSettingsCell) {
                    ((TextDetailSettingsCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof TextInfoPrivacyCell) {
                    ((TextInfoPrivacyCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof TextSettingsCell) {
                    ((TextSettingsCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof UnRegisteredContactCell) {
                    ((UnRegisteredContactCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof VoiceCell) {
                    ((VoiceCell) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof EmptyTextProgressView){
                    ((EmptyTextProgressView) view).setTextColor(Theme.getColor(currentKey));
                } else if (view instanceof NumberTextView) {
                    ((NumberTextView) view).setTextColor(Theme.getColor(currentKey));
                } else if(view instanceof EditTextBoldCursor){
                    ((EditTextBoldCursor) view).setTextColor(Theme.getColor(currentKey));
                } else if(view instanceof IconCell){
                    ((IconCell) view).setTextColor(Theme.getColor(Theme.key_icon));
                } else if(view instanceof SeekBarView){
                    ((SeekBarView) view).setColors(Theme.getColor(Theme.key_theme_color),Theme.getColor(Theme.key_theme_color));
                } else if(view instanceof ThemePreviewMessagesCell){
                    ((ThemePreviewMessagesCell) view).setColors(Theme.getColor(Theme.key_theme_color));
                } else if(view instanceof TextSizeCell){
                    ((TextSizeCell) view).setColors(Theme.getColor(Theme.key_theme_color));
                } else if(view instanceof ShadowSectionCell){
                    ((ShadowSectionCell) view).setColors(Theme.getColor(Theme.key_gray));
                }
            }
            if ((currentFlag & FLAG_CELLBACKGROUNDCOLOR) != 0) {
                if (!(view instanceof EmptyCell) && !(view instanceof ShadowSectionCell) && !(view instanceof TextInfoPrivacyCell)) {
                    view.setBackgroundColor(Theme.getColor(currentKey));
                }
            }
            if ((currentFlag & FLAG_BACKGROUND) != 0) {
                if (!(view instanceof EmptyCell) && !(view instanceof ShadowSectionCell) && !(view instanceof TextInfoPrivacyCell) && !(view instanceof ThemesHorizontalListCell)) {
                    view.setBackgroundColor(Theme.getColor(currentKey));
                }
            }
            if ((currentFlag & FLAG_BACKGROUNDFILTER) != 0) {
                if (view instanceof ImageView) {
                    ((ImageView) view).setColorFilter(Theme.getColor(currentKey));
                } else {
                    Drawable drawable = view.getBackground();
                    if (drawable instanceof CombinedDrawable) {
                        drawable = ((CombinedDrawable) drawable).getBackground();
                    }
                    if (drawable != null) {
                        if (drawable instanceof StateListDrawable || Build.VERSION.SDK_INT >= 21 && drawable instanceof RippleDrawable) {
                            Theme.setSelectorDrawableColor(drawable, Theme.getColor(currentKey), true);
                        } else if (drawable instanceof ShapeDrawable) {
                            ((ShapeDrawable) drawable).getPaint().setColor(Theme.getColor(currentKey));
                        } else {
                            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(currentKey), PorterDuff.Mode.MULTIPLY));
                        }
                    }
                }
            }
            if ((currentFlag & FLAG_PROGRESSBAR) != 0) {
                if (view instanceof EmptyTextProgressView){
                    ((EmptyTextProgressView) view).setProgressBarColor(Theme.getColor(currentKey));
                } else if (view instanceof RadialProgressView) {
                    ((RadialProgressView) view).setProgressColor(Theme.getColor(currentKey));
                } else if (view instanceof LineProgressView) {
                    ((LineProgressView) view).setProgressColor(Theme.getColor(currentKey));
                } else if (view instanceof SeekBarView) {
                    ((SeekBarView) view).setProgressColor(Theme.getColor(currentKey));
                } else if(view instanceof EditTextBoldCursor){
                    ((EditTextBoldCursor) view).setErrorLineColor(Theme.getColor(currentKey));
                }
            }
            if ((currentFlag & FLAG_CURSORCOLOR) != 0) {
                if (view instanceof EditTextBoldCursor) {
                    ((EditTextBoldCursor) view).setCursorColor(Theme.getColor(currentKey));
                }
            }
            if ((currentFlag & FLAG_HINTTEXTCOLOR) != 0) {
                if (view instanceof EditTextBoldCursor) {
                    ((EditTextBoldCursor) view).setHeaderHintColor(Theme.getColor(currentKey));
                    ((EditTextBoldCursor) view).setHintColor(Theme.getColor(currentKey));
                } else if (view instanceof EditText) {
                    ((EditText) view).setHintTextColor(Theme.getColor(currentKey));
                }
            }
            if ((currentFlag & FLAG_LISTGLOWCOLOR) != 0) {
                if (view instanceof ViewPager) {
                    setViewPagerEdgeEffectColor((ViewPager) view, Theme.getColor(currentKey));
                }
            }
            if ((currentFlag & FLAG_LISTGLOWCOLOR) != 0) {
                if (view instanceof ScrollView) {
                    setScrollViewEdgeEffectColor((ScrollView) view, Theme.getColor(currentKey));
                }
            }
            if ((currentFlag & FLAG_IMAGECOLOR) != 0) {
                if (view instanceof ImageView) {
                    Drawable drawable = ((ImageView) view).getDrawable();
                    if (drawable instanceof StateListDrawable || Build.VERSION.SDK_INT >= 21 && drawable instanceof RippleDrawable) {
                        Theme.setSelectorDrawableColor(drawable, Theme.getColor(currentKey), (currentFlag & FLAG_DRAWABLESELECTEDSTATE) != 0);
                    } else {
                        ((ImageView) view).setColorFilter(new PorterDuffColorFilter(Theme.getColor(currentKey), PorterDuff.Mode.MULTIPLY));
                    }
                } else if (view instanceof SimpleTextView) {
                    SimpleTextView textView = (SimpleTextView) view;
                    textView.setSideDrawablesColor(Theme.getColor(currentKey));
                } else if (view instanceof TextView) {
                    Drawable[] drawables = ((TextView) view).getCompoundDrawables();
                    if (drawables != null) {
                        for (int a = 0; a < drawables.length; a++) {
                            if (drawables[a] != null) {
                                drawables[a].setColorFilter(new PorterDuffColorFilter(Theme.getColor(currentKey), PorterDuff.Mode.MULTIPLY));
                            }
                        }
                    }
                }
            }
            if (delegate != null) {
                delegate.didSetColor();
            }
            view.invalidate();
        }
    }
}


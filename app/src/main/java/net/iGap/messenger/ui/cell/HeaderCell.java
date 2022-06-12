package net.iGap.messenger.ui.cell;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

import java.util.ArrayList;

public class HeaderCell extends FrameLayout {
    private final boolean isRtl = G.isAppRtl;
    private TextView textView;
    private SimpleTextView textView2;
    private int height = 40;

    public HeaderCell(Context context) {
        this(context, Theme.key_title_text, 21, 15, false);
    }

    public HeaderCell(Context context, int padding) {
        this(context, Theme.key_title_text, padding, 15, false);
    }

    public HeaderCell(Context context, String textColorKey, int padding, int topMargin, boolean text2) {
        super(context);

        textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        textView.setMinHeight(LayoutCreator.dp(height - topMargin));
        textView.setTextColor(Theme.getColor(textColorKey));
        textView.setTag(textColorKey);
        addView(textView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, padding, topMargin, padding, 0));

        if (text2) {
            textView2 = new SimpleTextView(getContext());
            textView2.setTextSize(13);
            textView2.setGravity((isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP);
            addView(textView2, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP, padding, 21, padding, 0));
        }

        ViewCompat.setAccessibilityHeading(this, true);
    }

    public void setHeight(int value) {
        textView.setMinHeight(LayoutCreator.dp(height = value) - ((FrameLayout.LayoutParams) textView.getLayoutParams()).topMargin);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        if (animators != null) {
            animators.add(ObjectAnimator.ofFloat(textView, "alpha", value ? 1.0f : 0.5f));
        } else {
            textView.setAlpha(value ? 1.0f : 0.5f);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public void setText2(CharSequence text) {
        if (textView2 == null) {
            return;
        }
        textView2.setText(text);
    }

    public TextView getTextView() {
        return textView;
    }

    public SimpleTextView getTextView2() {
        return textView2;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AccessibilityNodeInfo.CollectionItemInfo collection = info.getCollectionItemInfo();
            if (collection != null) {
                info.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collection.getRowIndex(), collection.getRowSpan(), collection.getColumnIndex(), collection.getColumnSpan(), true));
            }
        }
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
        if (textView2 != null) {
            textView2.setTextColor(color);
        }
        invalidate();
    }
}

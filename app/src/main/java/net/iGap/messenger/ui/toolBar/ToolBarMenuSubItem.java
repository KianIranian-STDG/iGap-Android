package net.iGap.messenger.ui.toolBar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.Theme;

public class ToolBarMenuSubItem extends FrameLayout {

    private TextView textView;
    private IconView iconView;
    private ImageView checkView;

    private int textColor;
    private int selectorColor;

    public ToolBarMenuSubItem(Context context) {
        this(context, false);
    }

    public ToolBarMenuSubItem(Context context, boolean needCheck) {
        super(context);
        textColor = Theme.getInstance().getTitleTextColor(context);
        selectorColor = Theme.getInstance().getDividerColor(context);
        setBackground(Theme.createSelectorDrawable(selectorColor, 2));
        setPadding(LayoutCreator.dp(18), 0, LayoutCreator.dp(18), 0);

        iconView = new IconView(context);
        iconView.setIconColor(textColor);
        addView(iconView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, 40, Gravity.CENTER_VERTICAL | (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT)));

        textView = new TextView(context);
        textView.setLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.LEFT);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(textColor);
        textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        addView(textView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL));

        if (needCheck) {
            checkView = new ImageView(context);
            checkView.setImageResource(R.drawable.round_check);
            checkView.setScaleType(ImageView.ScaleType.CENTER);
            checkView.setColorFilter(new PorterDuffColorFilter(Theme.getInstance().getTitleTextColor(context), PorterDuff.Mode.MULTIPLY));
            addView(checkView, LayoutCreator.createFrame(26, LayoutCreator.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(LayoutCreator.dp(48), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (checkView != null) {
            if (G.isAppRtl) {
                left = getPaddingRight();
            } else {
                left = getMeasuredWidth() - checkView.getMeasuredWidth() - getPaddingLeft();
            }
            checkView.layout(left, checkView.getTop(), left + checkView.getMeasuredWidth(), checkView.getBottom());
        }
    }

    public void setChecked(boolean checked) {
        if (checkView == null) {
            return;
        }
        checkView.setVisibility(checked ? VISIBLE : INVISIBLE);
    }

    public void setTextAndIcon(CharSequence text, int icon) {
        textView.setText(text);
        if (icon != 0) {
            iconView.setIcon(icon);
            iconView.setVisibility(VISIBLE);
            textView.setPadding(G.isAppRtl ? 0 : LayoutCreator.dp(43), 0, G.isAppRtl ? LayoutCreator.dp(43) : 0, 0);
        } else {
            iconView.setVisibility(INVISIBLE);
            textView.setPadding(0, 0, 0, 0);
        }
    }

    public void setColors(int colors) {
        setTextColor(colors);
        setIconColor(colors);
    }

    public void setTextColor(int textColor) {
        if (this.textColor != textColor) {
            textView.setTextColor(this.textColor = textColor);
        }
    }

    public void setIconColor(int iconColor) {
        if (this.textColor != iconColor) {
            iconView.setIconColor(iconColor);
        }
    }

    public void setIcon(int resId) {
        iconView.setIcon(resId);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public TextView getTextView() {
        return textView;
    }

    public void setSelectorColor(int selectorColor) {
        if (this.selectorColor != selectorColor) {
            setBackground(Theme.createSelectorDrawable(this.selectorColor = selectorColor, 2));
        }
    }
}

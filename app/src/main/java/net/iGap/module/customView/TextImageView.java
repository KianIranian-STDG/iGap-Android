package net.iGap.module.customView;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;

public class TextImageView extends LinearLayout {
    private TextView titleView;
    private ImageView iconView;

    public TextImageView(Context context) {
        super(context);

        setOrientation(VERTICAL);

        iconView = new AppCompatImageView(context);
        iconView.setScaleType(ImageView.ScaleType.CENTER);
        addView(iconView, LayoutCreator.createLinear(52, 52, Gravity.CENTER));

        titleView = new AppCompatTextView(context);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        titleView.setTextColor(Theme.getInstance().getTitleTextColor(context));
        titleView.setGravity(Gravity.CENTER);
        titleView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(titleView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));
    }

    public final void setText(@StringRes int resId) {
        if (titleView != null)
            titleView.setText(getContext().getResources().getText(resId));
    }

    public final void setText(CharSequence text) {
        if (titleView != null)
            titleView.setText(text);
    }

    public void setTextColor(@ColorInt int color) {
        if (titleView != null) {
            titleView.setTextColor(color);
        }
    }

    public void setImageResource(@DrawableRes int resId) {
        if (iconView != null)
            iconView.setImageResource(resId);
    }

    public void setViewColor(int color) {
        setIconColor(color);
        setTextColor(color);
    }

    public void setIconColor(int color) {
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        iconView.setColorFilter(porterDuffColorFilter);
    }
}

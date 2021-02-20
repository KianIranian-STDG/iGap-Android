package net.iGap.messenger.ui.components;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;

public class IconView extends AppCompatTextView {

    public IconView(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
        setTextColor(0xffffffff);
        setTypeface(ResourcesCompat.getFont(context, R.font.font_icon_new));
    }

    public void setIconColor(int color) {
        setTextColor(color);
    }

    public void setIcon(@StringRes int icon) {
        setText(getContext().getResources().getText(icon));
    }
}

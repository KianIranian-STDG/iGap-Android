package net.iGap.messenger.ui.components;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;

public class IconView extends AppCompatTextView {

    public IconView(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
        setTextColor(0xffffffff);
        setTypeface(ResourcesCompat.getFont(context, R.font.font_icon));
    }

    public void setIconColor(int color) {
        setTextColor(color);
    }

    public void setIcon(int icon) {
        try {
            getResources().getDrawable(icon);
            setBackgroundDrawable(getResources().getDrawable(icon));
        } catch (RuntimeException e) {
            setText(getResources().getText(icon));
        }
    }
}

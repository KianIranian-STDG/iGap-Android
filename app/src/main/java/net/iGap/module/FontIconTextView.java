package net.iGap.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;

public class FontIconTextView extends androidx.appcompat.widget.AppCompatTextView {
    public FontIconTextView(Context context) {
        super(context);
        init();
    }

    public FontIconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontIconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTypeface(ResourcesCompat.getFont(getContext(), R.font.font_icons));
        setGravity(Gravity.CENTER);
    }
}

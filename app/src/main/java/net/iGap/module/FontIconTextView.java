package net.iGap.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

import net.iGap.G;

public class FontIconTextView extends android.support.v7.widget.AppCompatTextView {
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
        setTypeface(G.typeface_iGap);
        setGravity(Gravity.CENTER);
    }
}
package com.iGap.module;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.iGap.G;

/**
 * Created by android3 on 2/21/2017.
 */

public class ColorImageView extends ImageView {
    public ColorImageView(Context context) {
        super(context);
        this.setColorFilter(Color.parseColor(G.attachmentColor));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public ColorImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setColorFilter(Color.parseColor(G.attachmentColor));
    }

    public ColorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setColorFilter(Color.parseColor(G.attachmentColor));
    }

    public ColorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setColorFilter(Color.parseColor(G.attachmentColor));
    }
}

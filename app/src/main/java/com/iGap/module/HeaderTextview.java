package com.iGap.module;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;
import com.iGap.G;

/**
 * Created by android3 on 2/21/2017.
 */

public class HeaderTextview extends TextView {

    public HeaderTextview(Context context) {
        super(context);
        this.setTextColor(Color.parseColor(G.headerTextColor));
    }

    public HeaderTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextColor(Color.parseColor(G.headerTextColor));
    }

    public HeaderTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTextColor(Color.parseColor(G.headerTextColor));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public HeaderTextview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setTextColor(Color.parseColor(G.headerTextColor));
    }
}

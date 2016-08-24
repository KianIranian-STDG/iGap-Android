package com.iGap.module;


import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.DimenRes;
import android.util.DisplayMetrics;
import android.view.Display;

public final class Utils {
    private Utils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    public static int getWindowWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int dpToPx(Context context, @DimenRes int res) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = (int) (context.getResources().getDimension(res) / displayMetrics.density);
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}

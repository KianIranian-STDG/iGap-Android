package net.iGap.libs.bottomNavigation.Util;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;

public class Utils {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static void darkModeHandler(View view) {
        if (G.isDarkTheme) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.background_setting_dark));
        } else {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.white));
        }
    }

    public static void darkModeHandler(TextView textView) {
        if (G.isDarkTheme) {
            textView.setTextColor(textView.getContext().getResources().getColor(R.color.white));
        } else {
            textView.setTextColor(textView.getContext().getResources().getColor(R.color.black));
        }
    }

    public static int darkModeHandler(Context context) {
        if (G.isDarkTheme) {
            return context.getResources().getColor(R.color.white);
        } else {
            return context.getResources().getColor(R.color.black);
        }
    }


    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
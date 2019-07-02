package net.iGap.libs.bottomNavigation.Util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;

import static net.iGap.G.context;

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

    public static void setBackgroundColor(View view) {
        if (G.isDarkTheme) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.white));
        } else {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.black));
        }
    }

    public static void setBackgroundColorGray(View view) {
        if (G.isDarkTheme) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.gray_300));
        } else {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.gray_3c));
        }
    }

    public static void darkModeHandlerGray(View view) {
        if (G.isDarkTheme) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.gray_4c));
        } else {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.bottomSheetHeaderLine));
        }
    }

    public static void darkModeHandlerGray(TextView textView) {
        if (G.isDarkTheme) {
            textView.setTextColor(textView.getContext().getResources().getColor(R.color.bottomSheetHeaderLine));
        } else {
            textView.setTextColor(textView.getContext().getResources().getColor(R.color.gray_4c));
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

    public static void setTextSize(TextView textView, int sizeSrc) {
        int mSize = getDimens(sizeSrc);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSize);
    }

    public static int getDimens(int dpSrc) {
        return (int) context.getResources().getDimension(dpSrc);
    }
}

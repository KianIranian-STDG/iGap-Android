package net.iGap.libs.bottomNavigation.Util;

import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.TextView;

import static net.iGap.G.context;

public class Utils {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static void setTextSize(TextView textView, int sizeSrc) {
        int mSize = getDimens(sizeSrc);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSize);
    }

    public static int getDimens(int dpSrc) {
        return (int) context.getResources().getDimension(dpSrc);
    }
}

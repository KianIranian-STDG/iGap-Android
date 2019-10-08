package net.iGap.libs.bottomNavigation.Util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

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

    public static void setTextSize(TextView textView, int sizeSrc) {
        int mSize = getDimens(sizeSrc);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSize);
    }

    public static int getDimens(int dpSrc) {
        return (int) context.getResources().getDimension(dpSrc);
    }
}

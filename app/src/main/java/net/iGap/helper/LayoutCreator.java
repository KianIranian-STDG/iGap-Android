package net.iGap.helper;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.iGap.G;

public class LayoutCreator {

    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;
    public static Context context = G.context;

    private static int getSize(float size) {
        return (int) (size < 0 ? size : dp(size));
    }

    public static FrameLayout.LayoutParams createFrame(int width, float height, int gravity, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getSize(width), getSize(height), gravity);
        layoutParams.setMargins(dp(leftMargin), dp(topMargin), dp(rightMargin), dp(bottomMargin));
        return layoutParams;
    }

    public static FrameLayout.LayoutParams createFrame(int width, int height, int gravity) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height), gravity);
    }

    public static FrameLayout.LayoutParams createFrame(int width, float height) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height));
    }

    public static RelativeLayout.LayoutParams createRelative(float width, float height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignParent, int alignRelative, int anchorRelative) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getSize(width), getSize(height));
        if (alignParent >= 0) {
            layoutParams.addRule(alignParent);
        }
        if (alignRelative >= 0 && anchorRelative >= 0) {
            layoutParams.addRule(alignRelative, anchorRelative);
        }
        layoutParams.leftMargin = dp(leftMargin);
        layoutParams.topMargin = dp(topMargin);
        layoutParams.rightMargin = dp(rightMargin);
        layoutParams.bottomMargin = dp(bottomMargin);
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int gravity, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
        layoutParams.setMargins(dp(leftMargin), dp(topMargin), dp(rightMargin), dp(bottomMargin));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
        layoutParams.setMargins(dp(leftMargin), dp(topMargin), dp(rightMargin), dp(bottomMargin));
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, int gravity, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height));
        layoutParams.setMargins(dp(leftMargin), dp(topMargin), dp(rightMargin), dp(bottomMargin));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height));
        layoutParams.setMargins(dp(leftMargin), dp(topMargin), dp(rightMargin), dp(bottomMargin));
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height));
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(context.getResources().getDisplayMetrics().density * value);
    }
}
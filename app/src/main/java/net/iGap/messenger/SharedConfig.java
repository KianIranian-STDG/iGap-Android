package net.iGap.messenger;

import android.content.SharedPreferences;

public class SharedConfig {
    public static int fontSize = 16;
    public static int bubbleRadius = 10;
    public static boolean useThreeLinesLayout;

    public static void setUseThreeLinesLayout(boolean value) {
        useThreeLinesLayout = value;
    }
}

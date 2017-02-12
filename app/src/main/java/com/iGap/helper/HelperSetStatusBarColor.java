package com.iGap.helper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.iGap.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import java.lang.reflect.Field;

/**
 * Created by android3 on 2/12/2017.
 */

public class HelperSetStatusBarColor {

    public static void setColor(Activity activity) {

        //
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //    Window window = activity.getWindow();
        //    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //    window.setStatusBarColor(activity.getResources().getColor((R.color.iGapColor)));
        //}else {
        //
        //    hackStatusBarColor(activity, R.color.iGapColor);
        //
        //}

        hackStatusBarColor(activity, R.color.iGapColor);
    }

    @SuppressLint("NewApi") @SuppressWarnings("deprecation") private static View hackStatusBarColor(final Activity act, final int colorResID) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {

                if (act.getWindow() != null) {

                    final ViewGroup vg = (ViewGroup) act.getWindow().getDecorView();
                    if (vg.getParent() == null && applyColoredStatusBar(act, colorResID)) {
                        final View statusBar = new View(act);

                        vg.post(new Runnable() {
                            @Override public void run() {

                                int statusBarHeight = (int) Math.ceil(25 * vg.getContext().getResources().getDisplayMetrics().density);
                                statusBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
                                statusBar.setBackgroundColor(act.getResources().getColor(colorResID));
                                //  statusBar.setId();
                                vg.addView(statusBar, 0);
                            }
                        });
                        return statusBar;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (act.getWindow() != null) {
            act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            act.getWindow().setStatusBarColor(act.getResources().getColor(colorResID));
        }
        return null;
    }

    private static boolean applyColoredStatusBar(Activity act, int colorResID) {
        final Window window = act.getWindow();
        final int flag;
        if (window != null) {
            View decor = window.getDecorView();
            if (decor != null) {
                flag = resolveTransparentStatusBarFlag(act);

                if (flag != 0) {
                    decor.setSystemUiVisibility(flag);
                    return true;
                } else
                //if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)

                {
                    //act.findViewById(android.R.id.content).setFitsSystemWindows(false);
                    //setTranslucentStatus(window, true);
                    //final SystemBarTintManager tintManager = new SystemBarTintManager(act);
                    //tintManager.setStatusBarTintEnabled(true);
                    //tintManager.setStatusBarTintColor(colorResID);

                    SystemBarTintManager tintManager = new SystemBarTintManager(act);
                    // enable status bar tint
                    tintManager.setStatusBarTintEnabled(true);
                    // enable navigation bar tint
                    tintManager.setNavigationBarTintEnabled(true);

                    tintManager.setTintColor(colorResID);
                }
            }
        }
        return false;
    }

    private static int resolveTransparentStatusBarFlag(Context ctx) {
        String[] libs = ctx.getPackageManager().getSystemSharedLibraryNames();
        String reflect = null;

        if (libs == null) return 0;

        final String SAMSUNG = "touchwiz";
        final String SONY = "com.sonyericsson.navigationbar";

        for (String lib : libs) {

            if (lib.equals(SAMSUNG)) {
                reflect = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND";
            } else if (lib.startsWith(SONY)) {
                reflect = "SYSTEM_UI_FLAG_TRANSPARENT";
            }
        }

        if (reflect == null) return 0;

        try {
            Field field = View.class.getField(reflect);
            if (field.getType() == Integer.TYPE) {
                return field.getInt(null);
            }
        } catch (Exception e) {
        }

        return 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT) private static void setTranslucentStatus(Window win, boolean on) {
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}

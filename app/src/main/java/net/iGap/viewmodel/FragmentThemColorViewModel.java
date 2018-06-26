package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.databinding.FragmentThemColorBinding;
import net.iGap.fragments.FragmentDarkTheme;
import net.iGap.fragments.FragmentNotificationAndSound;
import net.iGap.fragments.FragmentThemColor;
import net.iGap.fragments.FragmentThemColorCustom;
import net.iGap.helper.HelperFragment;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;

public class FragmentThemColorViewModel {

    private SharedPreferences sharedPreferences;
    private FragmentThemColor fragmentThemColor;
    private FragmentThemColorBinding fragmentThemColorBinding;


    public FragmentThemColorViewModel(FragmentThemColor fragmentThemColor, FragmentThemColorBinding fragmentThemColorBinding) {
        this.fragmentThemColor = fragmentThemColor;
        this.fragmentThemColorBinding = fragmentThemColorBinding;
        getInfo();
    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================


    public void onClickThemCustom(View v) {
        new HelperFragment(new FragmentThemColorCustom()).setReplace(false).load();
    }

    public void onClickThemeDark(View v) {
        new HelperFragment(FragmentDarkTheme.newInstance()).setReplace(false).load();

    }

    public void onClickThemeRed(View v) {
        setSetting(Config.RED, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemePink(View v) {
        setSetting(Config.PINK, false);
        Config.setThemeColor();
        resetApp();
    }

    private void setSetting(int config, boolean isDark) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHP_SETTING.KEY_THEME_COLOR, config);
        editor.putBoolean(SHP_SETTING.KEY_THEME_DARK, isDark);
        editor.apply();
    }

    public static void resetApp() {

        G.isUpdateNotificaionColorMain = true;
        G.isUpdateNotificaionColorChannel = true;
        G.isUpdateNotificaionColorGroup = true;
        G.isUpdateNotificaionColorChat = true;
        G.fragmentActivity.recreate();

        if (G.onRefreshActivity != null) {
            G.isRestartActivity = true;
            G.onRefreshActivity.refresh("");
        }
    }


    private void getInfo() {
        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        String appBarColor = sharedPreferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, Config.default_appBarColor);
        if (fragmentThemColor != null) {
            GradientDrawable circleCustomColor = (GradientDrawable) fragmentThemColorBinding.themeCustom2.getBackground();
            circleCustomColor.setColor(Color.parseColor(appBarColor));

            GradientDrawable circleDarkColor = (GradientDrawable) fragmentThemColorBinding.themeDark3.getBackground();
            circleDarkColor.setColor(Color.parseColor(Config.default_dark_appBarColor));

            GradientDrawable circleRedColor = (GradientDrawable) fragmentThemColorBinding.themeRed3.getBackground();
            circleRedColor.setColor(Color.parseColor(Config.default_red_appBarColor));

            GradientDrawable circlePinkColor = (GradientDrawable) fragmentThemColorBinding.themePink3.getBackground();
            circlePinkColor.setColor(Color.parseColor(Config.default_red_appBarColor));
        }

    }
}

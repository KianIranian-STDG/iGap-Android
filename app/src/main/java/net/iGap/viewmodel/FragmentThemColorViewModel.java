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

    public void onClickThemeDefault(View v) {
        setSetting(Config.DEFAULT, false);
        Config.setThemeColor();
        resetApp();
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

    public void onClickThemePurple(View v) {
        setSetting(Config.PURPLE, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeDeepPurple(View v) {
        setSetting(Config.DEEPPURPLE, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeIndigo(View v) {
        setSetting(Config.INDIGO, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeBlue(View v) {
        setSetting(Config.BLUE, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeLightBlue(View v) {
        setSetting(Config.LIGHT_BLUE, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeCyan(View v) {
        setSetting(Config.CYAN, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeTeal(View v) {
        setSetting(Config.TEAL, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeGreen(View v) {
        setSetting(Config.GREEN, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeLightGreen(View v) {
        setSetting(Config.LIGHT_GREEN, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeLime(View v) {
        setSetting(Config.LIME, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeYellow(View v) {
        setSetting(Config.YELLLOW, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeAmber(View v) {
        setSetting(Config.AMBER, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeOrange(View v) {
        setSetting(Config.ORANGE, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeDeepOrange(View v) {
        setSetting(Config.DEEP_ORANGE, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeBrown(View v) {
        setSetting(Config.BROWN, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeGrey(View v) {
        setSetting(Config.GREY, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeBlueGrey(View v) {
        setSetting(Config.BLUE_GREY, false);
        Config.setThemeColor();
        resetApp();
    }

    public void onClickThemeBlueGreyComplete(View v) {
        setSetting(Config.BLUE_GREY_COMPLETE, false);
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
            GradientDrawable circleCustomColor = (GradientDrawable) fragmentThemColorBinding.themeCustom1.getBackground();
            circleCustomColor.setColor(Color.parseColor(appBarColor));

            GradientDrawable circleDefaultColor = (GradientDrawable) fragmentThemColorBinding.themeDefault1.getBackground();
            circleDefaultColor.setColor(Color.parseColor(Config.default_appBarColor));

            GradientDrawable circleDarkColor = (GradientDrawable) fragmentThemColorBinding.themeDark1.getBackground();
            circleDarkColor.setColor(Color.parseColor(Config.default_dark_appBarColor));

            GradientDrawable circleRedColor = (GradientDrawable) fragmentThemColorBinding.themeRed1.getBackground();
            circleRedColor.setColor(Color.parseColor(Config.default_red_appBarColor));

            GradientDrawable circlePinkColor = (GradientDrawable) fragmentThemColorBinding.themePink1.getBackground();
            circlePinkColor.setColor(Color.parseColor(Config.default_Pink_appBarColor));

            GradientDrawable circlePurpleColor = (GradientDrawable) fragmentThemColorBinding.themePurple1.getBackground();
            circlePurpleColor.setColor(Color.parseColor(Config.default_purple_appBarColor));

            GradientDrawable circleDeepPurpleColor = (GradientDrawable) fragmentThemColorBinding.themeDeepPurple1.getBackground();
            circleDeepPurpleColor.setColor(Color.parseColor(Config.default_deepPurple_appBarColor));

            GradientDrawable circleDeepIndigoColor = (GradientDrawable) fragmentThemColorBinding.themeIndigo.getBackground();
            circleDeepIndigoColor.setColor(Color.parseColor(Config.default_indigo_appBarColor));

            GradientDrawable circleBlueColor = (GradientDrawable) fragmentThemColorBinding.themeBlue.getBackground();
            circleBlueColor.setColor(Color.parseColor(Config.default_blue_appBarColor));

            GradientDrawable circleLightBlueColor = (GradientDrawable) fragmentThemColorBinding.themeLightBlue.getBackground();
            circleLightBlueColor.setColor(Color.parseColor(Config.default_lightBlue_appBarColor));

            GradientDrawable circleCyanColor = (GradientDrawable) fragmentThemColorBinding.themeCyan.getBackground();
            circleCyanColor.setColor(Color.parseColor(Config.default_cyan_appBarColor));

            GradientDrawable circleTealColor = (GradientDrawable) fragmentThemColorBinding.themeTeal.getBackground();
            circleTealColor.setColor(Color.parseColor(Config.default_teal_appBarColor));

            GradientDrawable circleGreenColor = (GradientDrawable) fragmentThemColorBinding.themeGreen.getBackground();
            circleGreenColor.setColor(Color.parseColor(Config.default_green_appBarColor));

            GradientDrawable circleLightGreenColor = (GradientDrawable) fragmentThemColorBinding.themeLightGreen.getBackground();
            circleLightGreenColor.setColor(Color.parseColor(Config.default_lightGreen_appBarColor));

            GradientDrawable circleLimeColor = (GradientDrawable) fragmentThemColorBinding.themeLime.getBackground();
            circleLimeColor.setColor(Color.parseColor(Config.default_lime_appBarColor));

            GradientDrawable circleYellowColor = (GradientDrawable) fragmentThemColorBinding.themeYellow.getBackground();
            circleYellowColor.setColor(Color.parseColor(Config.default_yellow_appBarColor));

            GradientDrawable circleAmberColor = (GradientDrawable) fragmentThemColorBinding.themeAmber.getBackground();
            circleAmberColor.setColor(Color.parseColor(Config.default_amber_appBarColor));

            GradientDrawable circleOrangeColor = (GradientDrawable) fragmentThemColorBinding.themeOrange.getBackground();
            circleOrangeColor.setColor(Color.parseColor(Config.default_orange_appBarColor));

            GradientDrawable circleDeepOrangeColor = (GradientDrawable) fragmentThemColorBinding.themeDeepOrange.getBackground();
            circleDeepOrangeColor.setColor(Color.parseColor(Config.default_deepOrange_appBarColor));

            GradientDrawable circleBrownColor = (GradientDrawable) fragmentThemColorBinding.themeBrown.getBackground();
            circleBrownColor.setColor(Color.parseColor(Config.default_brown_appBarColor));

            GradientDrawable circleGreyColor = (GradientDrawable) fragmentThemColorBinding.themeGrey.getBackground();
            circleGreyColor.setColor(Color.parseColor(Config.default_grey_appBarColor));

            GradientDrawable circleBlueGreyColor = (GradientDrawable) fragmentThemColorBinding.themeBlueGrey.getBackground();
            circleBlueGreyColor.setColor(Color.parseColor(Config.default_blueGrey_appBarColor));

            GradientDrawable circleBlueGreyCompleteColor = (GradientDrawable) fragmentThemColorBinding.themeBlueGreyComplete.getBackground();
            circleBlueGreyCompleteColor.setColor(Color.parseColor(Config.default_blueGrey_appBarColor));


            switch (G.themeColor) {
                case Config.CUSTOM:
                    fragmentThemColorBinding.iconCustom.setVisibility(View.VISIBLE);
                    break;
                case Config.DEFAULT:

                    fragmentThemColorBinding.iconDefault.setVisibility(View.VISIBLE);
                    break;
                case Config.DARK:
                    fragmentThemColorBinding.iconDark.setVisibility(View.VISIBLE);
                    break;
                case Config.RED:
                    fragmentThemColorBinding.iconRed.setVisibility(View.VISIBLE);
                    break;
                case Config.PINK:
                    fragmentThemColorBinding.iconPink.setVisibility(View.VISIBLE);
                    break;
                case Config.PURPLE:
                    fragmentThemColorBinding.iconPurple.setVisibility(View.VISIBLE);
                    break;
                case Config.DEEPPURPLE:
                    fragmentThemColorBinding.iconDeepPurple.setVisibility(View.VISIBLE);
                    break;
                case Config.INDIGO:
                    fragmentThemColorBinding.iconIndigo.setVisibility(View.VISIBLE);
                    break;
                case Config.BLUE:
                    fragmentThemColorBinding.iconBlue.setVisibility(View.VISIBLE);
                    break;

                case Config.LIGHT_BLUE:
                    fragmentThemColorBinding.iconLightBlue.setVisibility(View.VISIBLE);
                    break;

                case Config.CYAN:
                    fragmentThemColorBinding.iconCyan.setVisibility(View.VISIBLE);
                    break;

                case Config.TEAL:
                    fragmentThemColorBinding.iconTeal.setVisibility(View.VISIBLE);
                    break;

                case Config.GREEN:
                    fragmentThemColorBinding.iconGreen.setVisibility(View.VISIBLE);
                    break;

                case Config.LIGHT_GREEN:
                    fragmentThemColorBinding.iconLightGreen.setVisibility(View.VISIBLE);
                    break;

                case Config.LIME:
                    fragmentThemColorBinding.iconLime.setVisibility(View.VISIBLE);
                    break;

                case Config.YELLLOW:
                    fragmentThemColorBinding.iconYellow.setVisibility(View.VISIBLE);
                    break;
                case Config.AMBER:
                    fragmentThemColorBinding.iconAmber.setVisibility(View.VISIBLE);
                    break;

                case Config.ORANGE:
                    fragmentThemColorBinding.iconOrange.setVisibility(View.VISIBLE);
                    break;

                case Config.DEEP_ORANGE:
                    fragmentThemColorBinding.iconDeepOrange.setVisibility(View.VISIBLE);
                    break;

                case Config.BROWN:
                    fragmentThemColorBinding.iconBrown.setVisibility(View.VISIBLE);

                    break;
                case Config.GREY:

                    fragmentThemColorBinding.iconGrey.setVisibility(View.VISIBLE);
                    break;
                case Config.BLUE_GREY:

                    fragmentThemColorBinding.iconBlueGrey.setVisibility(View.VISIBLE);
                    break;
                case Config.BLUE_GREY_COMPLETE:
                    fragmentThemColorBinding.iconBlueGreyComplete.setVisibility(View.VISIBLE);
                    break;
            }


        }

    }
}

package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.module.DeprecatedTheme;
import net.iGap.databinding.FragmentThemColorBinding;
import net.iGap.fragments.FragmentThemColor;
import net.iGap.model.ChangeTheme;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;

public class FragmentThemColorViewModel extends ViewModel {

    private SharedPreferences sharedPreferences;
    private FragmentThemColor fragmentThemColor;
    private FragmentThemColorBinding fragmentThemColorBinding;
    public MutableLiveData<Boolean> goToThemeColorCustomPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToDarkThemePage = new MutableLiveData<>();
    public MutableLiveData<ChangeTheme> showDialogChangeTheme = new MutableLiveData<>();
    public MutableLiveData<Boolean> reCreateApp = new MutableLiveData<>();


    public FragmentThemColorViewModel(FragmentThemColor fragmentThemColor, FragmentThemColorBinding fragmentThemColorBinding) {
        this.fragmentThemColor = fragmentThemColor;
        this.fragmentThemColorBinding = fragmentThemColorBinding;
        getInfo();
    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================


    public void onClickThemCustom(View v) {
        goToThemeColorCustomPage.setValue(true);
    }

    public void onClickThemeDefault(View v) {
        setSetting(DeprecatedTheme.DEFAULT, false);

    }

    public void onClickThemeDark(View v) {
        goToDarkThemePage.setValue(true);
    }

    public void onClickThemeRed(View v) {
        setSetting(DeprecatedTheme.RED, false);

    }

    public void onClickThemePink(View v) {
        setSetting(DeprecatedTheme.PINK, false);
    }

    public void onClickThemePurple(View v) {
        setSetting(DeprecatedTheme.PURPLE, false);
    }

    public void onClickThemeDeepPurple(View v) {

    }

    public void onClickThemeIndigo(View v) {
    }

    public void onClickThemeBlue(View v) {
        setSetting(DeprecatedTheme.BLUE, false);
    }

    public void onClickThemeLightBlue(View v) {

    }

    public void onClickThemeCyan(View v) {

    }

    public void onClickThemeTeal(View v) {

    }

    public void onClickThemeGreen(View v) {
        setSetting(DeprecatedTheme.GREEN, false);
    }

    public void onClickThemeLightGreen(View v) {

    }

    public void onClickThemeLime(View v) {

    }

    public void onClickThemeYellow(View v) {

    }

    public void onClickThemeAmber(View v) {
        setSetting(DeprecatedTheme.AMBER, false);
    }

    public void onClickThemeOrange(View v) {
        setSetting(DeprecatedTheme.ORANGE, false);
    }

    public void onClickThemeDeepOrange(View v) {

    }

    public void onClickThemeBrown(View v) {

    }

    public void onClickThemeGrey(View v) {
        setSetting(DeprecatedTheme.GREY, false);

    }

    public void onClickThemeBlueGrey(View v) {

    }

    public void onClickThemeBlueGreyComplete(View v) {

    }

    public void onClickThemeIndigoComplete(View v) {

    }

    public void onClickThemeBrownComplete(View v) {

    }

    public void onClickThemeTealComplete(View v) {

    }

    public void onClickThemeGreyComplete(View v) {

    }

    private void setSetting(int config, boolean isDark) {
        showDialogChangeTheme.setValue(new ChangeTheme(config, isDark));
    }

    public void setNewTheme(ChangeTheme newTheme, boolean applyColorsToCustomize) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHP_SETTING.KEY_THEME_COLOR, newTheme.getConfig());
        editor.putBoolean(SHP_SETTING.KEY_THEME_DARK, newTheme.isDark());
        editor.apply();
        if (applyColorsToCustomize) {
//           editor.putString(SHP_SETTING.KEY_APP_BAR_COLOR, G.appBarColor);
//           editor.putString(SHP_SETTING.KEY_NOTIFICATION_COLOR, G.notificationColor);
//            editor.putString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, G.toggleButtonColor);
//            editor.putString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, G.attachmentColor);
//            editor.putString(SHP_SETTING.KEY_FONT_HEADER_COLOR, G.headerTextColor);
//            editor.putString(SHP_SETTING.KEY_PROGRES_COLOR, G.progressColor);
            editor.apply();
        }
        reCreateApp.setValue(true);
    }

    private void getInfo() {
        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        String appBarColor = sharedPreferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, DeprecatedTheme.default_appBarColor);
        if (fragmentThemColor != null) {
            GradientDrawable circleCustomColor = (GradientDrawable) fragmentThemColorBinding.themeCustom1.getBackground();
            circleCustomColor.setColor(Color.parseColor(appBarColor));

            GradientDrawable circleDefaultColor = (GradientDrawable) fragmentThemColorBinding.themeDefault1.getBackground();
            circleDefaultColor.setColor(Color.parseColor(DeprecatedTheme.default_appBarColor));

            GradientDrawable circleDarkColor = (GradientDrawable) fragmentThemColorBinding.themeDark1.getBackground();
            circleDarkColor.setColor(Color.parseColor(DeprecatedTheme.default_dark_appBarColor));

            GradientDrawable circleRedColor = (GradientDrawable) fragmentThemColorBinding.themeRed1.getBackground();
            circleRedColor.setColor(Color.parseColor(DeprecatedTheme.default_red_appBarColor));

            GradientDrawable circlePinkColor = (GradientDrawable) fragmentThemColorBinding.themePink1.getBackground();
            circlePinkColor.setColor(Color.parseColor(DeprecatedTheme.default_Pink_appBarColor));

            GradientDrawable circlePurpleColor = (GradientDrawable) fragmentThemColorBinding.themePurple1.getBackground();
            circlePurpleColor.setColor(Color.parseColor(DeprecatedTheme.default_purple_appBarColor));

            GradientDrawable circleDeepPurpleColor = (GradientDrawable) fragmentThemColorBinding.themeDeepPurple1.getBackground();
            circleDeepPurpleColor.setColor(Color.parseColor(DeprecatedTheme.default_deepPurple_appBarColor));

            GradientDrawable circleDeepIndigoColor = (GradientDrawable) fragmentThemColorBinding.themeIndigo.getBackground();
            circleDeepIndigoColor.setColor(Color.parseColor(DeprecatedTheme.default_indigo_appBarColor));

            GradientDrawable circleBlueColor = (GradientDrawable) fragmentThemColorBinding.themeBlue.getBackground();
            circleBlueColor.setColor(Color.parseColor(DeprecatedTheme.default_blue_appBarColor));

            GradientDrawable circleLightBlueColor = (GradientDrawable) fragmentThemColorBinding.themeLightBlue.getBackground();
            circleLightBlueColor.setColor(Color.parseColor(DeprecatedTheme.default_lightBlue_appBarColor));

            GradientDrawable circleCyanColor = (GradientDrawable) fragmentThemColorBinding.themeCyan.getBackground();
            circleCyanColor.setColor(Color.parseColor(DeprecatedTheme.default_cyan_appBarColor));

            GradientDrawable circleTealColor = (GradientDrawable) fragmentThemColorBinding.themeTeal.getBackground();
            circleTealColor.setColor(Color.parseColor(DeprecatedTheme.default_teal_appBarColor));

            GradientDrawable circleGreenColor = (GradientDrawable) fragmentThemColorBinding.themeGreen.getBackground();
            circleGreenColor.setColor(Color.parseColor(DeprecatedTheme.default_green_appBarColor));

            GradientDrawable circleLightGreenColor = (GradientDrawable) fragmentThemColorBinding.themeLightGreen.getBackground();
            circleLightGreenColor.setColor(Color.parseColor(DeprecatedTheme.default_lightGreen_appBarColor));

            GradientDrawable circleLimeColor = (GradientDrawable) fragmentThemColorBinding.themeLime.getBackground();
            circleLimeColor.setColor(Color.parseColor(DeprecatedTheme.default_lime_appBarColor));

            GradientDrawable circleYellowColor = (GradientDrawable) fragmentThemColorBinding.themeYellow.getBackground();
            circleYellowColor.setColor(Color.parseColor(DeprecatedTheme.default_yellow_appBarColor));

            GradientDrawable circleAmberColor = (GradientDrawable) fragmentThemColorBinding.themeAmber.getBackground();
            circleAmberColor.setColor(Color.parseColor(DeprecatedTheme.default_amber_appBarColor));

            GradientDrawable circleOrangeColor = (GradientDrawable) fragmentThemColorBinding.themeOrange.getBackground();
            circleOrangeColor.setColor(Color.parseColor(DeprecatedTheme.default_orange_appBarColor));

            GradientDrawable circleDeepOrangeColor = (GradientDrawable) fragmentThemColorBinding.themeDeepOrange.getBackground();
            circleDeepOrangeColor.setColor(Color.parseColor(DeprecatedTheme.default_deepOrange_appBarColor));

            GradientDrawable circleBrownColor = (GradientDrawable) fragmentThemColorBinding.themeBrown.getBackground();
            circleBrownColor.setColor(Color.parseColor(DeprecatedTheme.default_brown_appBarColor));

            GradientDrawable circleGreyColor = (GradientDrawable) fragmentThemColorBinding.themeGrey.getBackground();
            circleGreyColor.setColor(Color.parseColor(DeprecatedTheme.default_grey_appBarColor));

            GradientDrawable circleBlueGreyColor = (GradientDrawable) fragmentThemColorBinding.themeBlueGrey.getBackground();
            circleBlueGreyColor.setColor(Color.parseColor(DeprecatedTheme.default_blueGrey_appBarColor));

            GradientDrawable circleBlueGreyCompleteColor = (GradientDrawable) fragmentThemColorBinding.themeBlueGreyComplete.getBackground();
            circleBlueGreyCompleteColor.setColor(Color.parseColor(DeprecatedTheme.default_blueGrey_appBarColor));

            GradientDrawable circleIndigoCompleteColor = (GradientDrawable) fragmentThemColorBinding.themeIndigoComplete.getBackground();
            circleIndigoCompleteColor.setColor(Color.parseColor(DeprecatedTheme.default_indigo_appBarColor));

            GradientDrawable circleBrownCompleteColor = (GradientDrawable) fragmentThemColorBinding.themeBrownComplete.getBackground();
            circleBrownCompleteColor.setColor(Color.parseColor(DeprecatedTheme.default_brown_appBarColor));

            GradientDrawable circleTealCompleteColor = (GradientDrawable) fragmentThemColorBinding.themeTealComplete.getBackground();
            circleTealCompleteColor.setColor(Color.parseColor(DeprecatedTheme.default_teal_appBarColor));

            GradientDrawable circleGreyCompleteColor = (GradientDrawable) fragmentThemColorBinding.themeGreyComplete.getBackground();
            circleGreyCompleteColor.setColor(Color.parseColor(DeprecatedTheme.default_grey_appBarColor));


            /*switch (G.themeColor) {
                case DeprecatedTheme.DEFAULT:
                    fragmentThemColorBinding.iconDefault.setVisibility(View.VISIBLE);
                    break;
                case DeprecatedTheme.DARK:
                    fragmentThemColorBinding.iconDark.setVisibility(View.VISIBLE);
                    break;
                case DeprecatedTheme.RED:
                    fragmentThemColorBinding.iconRed.setVisibility(View.VISIBLE);
                    break;
                case DeprecatedTheme.PINK:
                    fragmentThemColorBinding.iconPink.setVisibility(View.VISIBLE);
                    break;
                case DeprecatedTheme.PURPLE:
                    fragmentThemColorBinding.iconPurple.setVisibility(View.VISIBLE);
                    break;
                case DeprecatedTheme.BLUE:
                    fragmentThemColorBinding.iconBlue.setVisibility(View.VISIBLE);
                    break;
                case DeprecatedTheme.GREEN:
                    fragmentThemColorBinding.iconGreen.setVisibility(View.VISIBLE);
                    break;
                case DeprecatedTheme.AMBER:
                    fragmentThemColorBinding.iconAmber.setVisibility(View.VISIBLE);
                    break;

                case DeprecatedTheme.ORANGE:
                    fragmentThemColorBinding.iconOrange.setVisibility(View.VISIBLE);
                    break;
                case DeprecatedTheme.GREY:
                    fragmentThemColorBinding.iconGrey.setVisibility(View.VISIBLE);
                    break;
            }*/


        }

    }
}

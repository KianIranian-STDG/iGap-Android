package net.iGap.viewmodel;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TimePicker;

import net.iGap.G;
import net.iGap.fragments.FragmentDarkTheme;
import net.iGap.module.SHP_SETTING;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */
public class FragmentDarkThemeViewModel {

    private boolean isDisable;
    private boolean isAuto;
    private boolean isScheduled;
    private SharedPreferences sharedPreferences;
    private FragmentDarkTheme fragmentDarkTheme;

    public FragmentDarkThemeViewModel(FragmentDarkTheme fragmentDarkTheme) {
        this.fragmentDarkTheme = fragmentDarkTheme;
        getInfo();
    }

    private void getInfo() {

        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        isDisable = sharedPreferences.getBoolean(SHP_SETTING.KEY_DISABLE_TIME_DARK_THEME, true);

        if (isDisable) {
            isAuto = false;
            isScheduled = false;
        } else {
            isAuto = sharedPreferences.getBoolean(SHP_SETTING.KEY_IS_AUTOMATIC_TIME_DARK_THEME, false);
            isScheduled = !isAuto;
        }


    }

    public void onClickDisable(View v) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_DISABLE_TIME_DARK_THEME, true);
        editor.putBoolean(SHP_SETTING.KEY_IS_AUTOMATIC_TIME_DARK_THEME, false);
        editor.apply();
        isDisable = true;
        isAuto = false;
        isScheduled = false;


    }

    public void onClickScheduled(View v) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_DISABLE_TIME_DARK_THEME, false);
        editor.putBoolean(SHP_SETTING.KEY_IS_AUTOMATIC_TIME_DARK_THEME, true);
        editor.apply();
        isDisable = false;
        isAuto = true;
        isScheduled = false;

    }

    public void onClickAutomatic(View v) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_DISABLE_TIME_DARK_THEME, false);
        editor.putBoolean(SHP_SETTING.KEY_IS_AUTOMATIC_TIME_DARK_THEME, false);
        editor.apply();
        isDisable = false;
        isAuto = false;
        isScheduled = true;
    }

    private void onClickFromTime(View v) {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(G.currentActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_SELECTED_HOUR_FROM, selectedHour);
                editor.putInt(SHP_SETTING.KEY_SELECTED_MINUTE_FROM, selectedMinute);
                editor.apply();

//                eReminderTime.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    private void onClickToTime(View v) {

        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(G.currentActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_SELECTED_HOUR_TO, selectedHour);
                editor.putInt(SHP_SETTING.KEY_SELECTED_MINUTE_TO, selectedMinute);
                editor.apply();

//                eReminderTime.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }


    public boolean isAutoDarkTheme() {
        return isAuto;
    }

    public boolean isScheduledDarkTheme() {
        return isScheduled;
    }

    public boolean isDisableDarkTheme() {
        return isDisable;
    }


}

package net.iGap.viewmodel;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TimePicker;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;

/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */
public class FragmentDarkThemeViewModel extends ViewModel {

    private boolean isDisable;
    private boolean isAuto;
    private boolean isScheduled;
    private SharedPreferences sharedPreferences;

    public ObservableField<String> callbackFromTime = new ObservableField<>("");
    public ObservableField<String> callbackToTime = new ObservableField<>("");
    public ObservableField<Integer> isScheduledDarkTheme = new ObservableField<>();
    public ObservableField<Integer> isAutoDarkTheme = new ObservableField<>();
    public ObservableField<Integer> isDisableDarkTheme = new ObservableField<>();
    public MutableLiveData<Boolean> resetApp = new MutableLiveData<>();


    public FragmentDarkThemeViewModel() {
        getInfo();
    }

    private void getInfo() {

        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        isDisable = sharedPreferences.getBoolean(SHP_SETTING.KEY_DISABLE_TIME_DARK_THEME, true);

        int hour_To = sharedPreferences.getInt(SHP_SETTING.KEY_SELECTED_HOUR_TO, 8);
        int minute_To = sharedPreferences.getInt(SHP_SETTING.KEY_SELECTED_MINUTE_TO, 10);
        callbackToTime.set("" + hour_To + ":" + minute_To);

        int hour_From = sharedPreferences.getInt(SHP_SETTING.KEY_SELECTED_HOUR_FROM, 22);
        int minute_From = sharedPreferences.getInt(SHP_SETTING.KEY_SELECTED_MINUTE_FROM, 10);
        callbackFromTime.set("" + hour_From + ":" + minute_From);

        if (isDisable) {
            isDisableDarkTheme.set(View.VISIBLE);
            isAutoDarkTheme.set(View.GONE);
            isScheduledDarkTheme.set(View.GONE);
        } else {
            isAuto = sharedPreferences.getBoolean(SHP_SETTING.KEY_IS_AUTOMATIC_TIME_DARK_THEME, false);
            isDisableDarkTheme.set(View.GONE);
            if (isAuto) {
                isAutoDarkTheme.set(View.VISIBLE);
                isScheduledDarkTheme.set(View.GONE);
            } else {
                isAutoDarkTheme.set(View.GONE);
                isScheduledDarkTheme.set(View.VISIBLE);
            }
        }


    }

    public void onClickSetTheme(View view) {
        SharedPreferences.Editor editor;
        if (sharedPreferences == null) {
            SharedPreferences sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            editor = sharedPreferences.edit();
        } else {
            editor = sharedPreferences.edit();
        }
        editor.putInt(SHP_SETTING.KEY_THEME_COLOR, Theme.DARK);
        editor.putBoolean(SHP_SETTING.KEY_THEME_DARK, true);
        editor.apply();

        /*Theme.setThemeColor();*/
        resetApp.setValue(true);

    }

    public void onClickDisable(View v) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_DISABLE_TIME_DARK_THEME, true);
        editor.putBoolean(SHP_SETTING.KEY_IS_AUTOMATIC_TIME_DARK_THEME, false);
        editor.apply();
        isDisableDarkTheme.set(View.VISIBLE);
        isScheduledDarkTheme.set(View.GONE);
        isAutoDarkTheme.set(View.GONE);
    }

    public void onClickScheduled(View v) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_DISABLE_TIME_DARK_THEME, false);
        editor.putBoolean(SHP_SETTING.KEY_IS_AUTOMATIC_TIME_DARK_THEME, false);
        editor.apply();

        isDisableDarkTheme.set(View.GONE);
        isScheduledDarkTheme.set(View.VISIBLE);
        isAutoDarkTheme.set(View.GONE);

    }

    public void onClickAutomatic(View v) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_DISABLE_TIME_DARK_THEME, false);
        editor.putBoolean(SHP_SETTING.KEY_IS_AUTOMATIC_TIME_DARK_THEME, true);
        editor.apply();

        isDisableDarkTheme.set(View.GONE);
        isScheduledDarkTheme.set(View.GONE);
        isAutoDarkTheme.set(View.VISIBLE);
    }

    public void onClickFromTime(View v) {
        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        int hour = sharedPreferences.getInt(SHP_SETTING.KEY_SELECTED_HOUR_FROM, 8);
        int minute = sharedPreferences.getInt(SHP_SETTING.KEY_SELECTED_MINUTE_FROM, 0);

        TimePickerDialog mTimePicker = new TimePickerDialog(G.currentActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                long fNow = (selectedHour * 3600000) + (selectedMinute * 60000);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_SELECTED_HOUR_FROM, selectedHour);
                editor.putInt(SHP_SETTING.KEY_SELECTED_MINUTE_FROM, selectedMinute);
                editor.putLong(SHP_SETTING.KEY_SELECTED_MILISECOND_FROM, fNow);
                editor.apply();
                callbackFromTime.set("" + selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(G.context.getResources().getString(R.string.Select_Time));
        mTimePicker.show();

    }

    public void onClickToTime(View v) {

        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        int hour = sharedPreferences.getInt(SHP_SETTING.KEY_SELECTED_HOUR_TO, 8);
        int minute = sharedPreferences.getInt(SHP_SETTING.KEY_SELECTED_MINUTE_TO, 0);

        TimePickerDialog mTimePicker = new TimePickerDialog(G.currentActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                long fNow = (selectedHour * 3600000) + (selectedMinute * 60000);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_SELECTED_HOUR_TO, selectedHour);
                editor.putInt(SHP_SETTING.KEY_SELECTED_MINUTE_TO, selectedMinute);
                editor.putLong(SHP_SETTING.KEY_SELECTED_MILISECOND_TO, fNow);
                editor.apply();
                callbackToTime.set("" + selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(G.context.getResources().getString(R.string.Select_Time));
        mTimePicker.show();

    }


}

package com.iGap.helper;

import android.content.SharedPreferences;
import android.text.format.DateUtils;

import com.iGap.G;
import com.iGap.libs.CalendarTools;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.TimeUtils;

import java.util.Calendar;

/**
 * Created by android3 on 12/11/2016.
 */

public class HelperCalander {

    public static String getPersianCalander(int year, int mounth, int day) {

        CalendarTools convertTime = new CalendarTools();
        convertTime.GregorianToPersian(year, mounth, day);

        return convertTime.getYear() + "/" + convertTime.getMonth() + "/" + convertTime.getDay();
    }

    public static String getPersianCalander(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return getPersianCalander(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static boolean isTimeHijri() {

        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, G.context.MODE_PRIVATE);
        int result = sharedPreferences.getInt(SHP_SETTING.KEY_ENABLE_DATA_SHAMS, 0);

        if (result == 1) return true;

        return false;
    }

    public static String checkHijriAndReturnTime(long time) {

        String result = "";

        if (isTimeHijri()) {
            result = getPersianCalander(time * DateUtils.SECOND_IN_MILLIS);
        } else {
            result = TimeUtils.toLocal(time * DateUtils.SECOND_IN_MILLIS, "dd MMM yyyy");
        }

        return result;
    }

    public static String milladyDate(long time) {
        return TimeUtils.toLocal(time, "dd_MM_yyyy");
    }

}

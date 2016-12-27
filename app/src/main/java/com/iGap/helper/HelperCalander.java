package com.iGap.helper;

import android.content.SharedPreferences;
import android.text.format.DateUtils;
import com.iGap.G;
import com.iGap.R;
import com.iGap.libs.CalendarTools;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.TimeUtils;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by android3 on 12/11/2016.
 */

public class HelperCalander {

    public static String getPersianCalander(int year, int mounth, int day) {

        CalendarTools convertTime = new CalendarTools();
        convertTime.GregorianToPersian(year, mounth, day);

        String t = convertTime.getYear() + "/" + (Integer.parseInt(convertTime.getMonth()) + 1) + "/" + convertTime.getDay();

        return convertToUnicodeFarsiNumber(t);
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

    public static String getPersianMonthName(int month) {

        String result = "";

        switch (month) {

            case 1:
                result = G.context.getString(R.string.farvardin);
                break;
            case 2:
                result = G.context.getString(R.string.ordibehst);
                break;
            case 3:
                result = G.context.getString(R.string.khordad);
                break;
            case 4:
                result = G.context.getString(R.string.tir);
                break;
            case 5:
                result = G.context.getString(R.string.mordad);
                break;
            case 6:
                result = G.context.getString(R.string.shahrivar);
                break;
            case 7:
                result = G.context.getString(R.string.mehr);
                break;
            case 8:
                result = G.context.getString(R.string.aban);
                break;
            case 9:
                result = G.context.getString(R.string.azar);
                break;
            case 10:
                result = G.context.getString(R.string.dey);
                break;
            case 11:
                result = G.context.getString(R.string.bahman);
                break;
            case 12:
                result = G.context.getString(R.string.esfand);
                break;
        }

        return result;
    }

    public static String convertToUnicodeFarsiNumber(String text) {

        String[] persianNumbers = new String[] { "۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹" };

        if (text.length() == 0) {
            return "";
        }

        String out = "";

        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') {
                int number = Integer.parseInt(String.valueOf(c));
                out += persianNumbers[number];
            } else if (c == '٫') {
                out += '،';
            } else {
                out += c;
            }
        }

        return out;
    }

    public static String getTimeForMainRoom(long time) {

        Calendar current = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));

        String output = "";

        if (current.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && current.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {

            output = TimeUtils.toLocal(time, G.ROOM_LAST_MESSAGE_TIME);
        } else if (current.get(Calendar.DAY_OF_YEAR) < (date.get(Calendar.DAY_OF_YEAR) + 7)) {

            if (HelperCalander.isTimeHijri()) {
                output = getPersianStringDay(date.get(Calendar.DAY_OF_WEEK));
            } else {
                output = TimeUtils.toLocal(date.getTimeInMillis(), "EEE");
            }
        } else {

            if (HelperCalander.isTimeHijri()) {

                CalendarTools convertTime = new CalendarTools();
                convertTime.GregorianToPersian(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));

                output = HelperCalander.getPersianMonthName(Integer.parseInt(convertTime.getMonth()) + 1) + " " + convertTime.getDay();
                output = HelperCalander.convertToUnicodeFarsiNumber(output);
            } else {
                output = TimeUtils.toLocal(date.getTimeInMillis(), "MMM dd");
            }
        }

        return output;
    }

    private static String getPersianStringDay(int dayOfWeek) {

        String result = "";

        switch (dayOfWeek) {

            case 1:
                result = G.context.getString(R.string.sunday);
                break;
            case 2:
                result = G.context.getString(R.string.monday);
                break;
            case 3:
                result = G.context.getString(R.string.tuesday);
                break;
            case 4:
                result = G.context.getString(R.string.wednesday);
                break;
            case 5:
                result = G.context.getString(R.string.thursday);
                break;
            case 6:
                result = G.context.getString(R.string.friday);
                break;
            case 7:
                result = G.context.getString(R.string.saturday);
                break;
        }

        return result;
    }

}

package com.iGap.module;

/*
 * Copyright 2016 Alireza Eskandarpour Shoferi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import com.iGap.R;
import com.iGap.helper.HelperCalander;
import com.iGap.libs.CalendarTools;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {
    private TimeUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    /**
     * convert unix time to local time
     *
     * @param unixTime unix time is 13 characters (milliseconds), if you passed seconds, remember
     *                 to
     *                 multiply by 1000L
     * @param format   String format
     * @return String formatted time in local
     */
    public static String toLocal(long unixTime, String format) {
        return new SimpleDateFormat(format, Locale.US).format(unixTime);
    }

    /**
     * get current local time in milliseconds
     *
     * @return Long local time in milliseconds
     */
    public static long currentLocalTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    private static Calendar getYesterdayCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal;
    }

    public static String getChatSettingsTimeAgo(Context context, Date comingDate) {
        Calendar current = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        Calendar yesterday = getYesterdayCalendar();
        date.setTime(comingDate);

        String output = "";

        if (current.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && current.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
            output = context.getString(R.string.today);
        } else if (yesterday.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
            output = context.getString(R.string.yesterday);
        } else {

            if (HelperCalander.isTimeHijri()) {

                CalendarTools convertTime = new CalendarTools();
                convertTime.GregorianToPersian(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));

                output = convertTime.getYear() + " " + HelperCalander.getPersianMonthName(Integer.parseInt(convertTime.getMonth()) + 1) + " " + convertTime.getDay();
                output = HelperCalander.convertToUnicodeFarsiNumber(output);
            } else {
                output = TimeUtils.toLocal(date.getTimeInMillis(), "dd MMM yyyy");
            }
        }

        //else //noinspection WrongConstant
        //    if (current.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR) + 1 && current.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
        //        output = String.format("%1$s %2$s", new SimpleDateFormat("EEE", Locale.getDefault()).format(date.getTimeInMillis()), date.get(Calendar.DAY_OF_MONTH));
        //    } else if (current.get(Calendar.YEAR) < date.get(Calendar.YEAR)) {
        //        output = String.format("%1$s-%2$s-%3$s", new SimpleDateFormat("MM", Locale.getDefault()).format(date.getTimeInMillis()),
        //            date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.YEAR));
        //    } else {
        //        output = String.format("%1$s %2$s", new SimpleDateFormat("MMMM", Locale.getDefault()).format(date.getTimeInMillis()), date.get(Calendar.DAY_OF_MONTH));
        //    }

        return output;
    }
}
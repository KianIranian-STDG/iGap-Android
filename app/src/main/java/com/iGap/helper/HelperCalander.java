package com.iGap.helper;

import com.iGap.libs.CalendarTools;
import java.util.Calendar;

/**
 * Created by android3 on 12/11/2016.
 */

public class HelperCalander {

    public static class StructCalander {

        int year = 0;
        String month = "0";
        String daye = "0";
    }

    public static StructCalander getPersianCalander(int year, int mounth, int day) {

        CalendarTools convertTime = new CalendarTools();
        convertTime.GregorianToPersian(year, mounth, day);

        StructCalander sc = new StructCalander();

        sc.year = convertTime.getYear();
        sc.month = convertTime.getMonth();
        sc.daye = convertTime.getDay();

        return sc;
    }

    public static StructCalander getPersianCalander(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return getPersianCalander(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}

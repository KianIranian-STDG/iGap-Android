package net.iGap.libs.persianDatePicker;


import net.iGap.libs.persianDatePicker.util.PersianCalendar;

/**
 * Created by aliabdolahi on 1/23/17.
 */

public interface Listener {
    void onDateSelected(PersianCalendar persianCalendar);

    void onDismissed();
}

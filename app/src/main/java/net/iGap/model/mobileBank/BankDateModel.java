package net.iGap.model.mobileBank;


import net.iGap.module.mobileBank.JalaliCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BankDateModel {

    private String monthName;
    private JalaliCalendar date;
    private boolean isSelected;
    private boolean isActive;

    public BankDateModel(String monthName, JalaliCalendar date, boolean isSelected, boolean isActive) {
        this.monthName = monthName;
        this.date = date;
        this.isSelected = isSelected;
        this.isActive = isActive;
    }

    public String getStartMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date.getTime());
    }

    public String getEndMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JalaliCalendar tmp = new JalaliCalendar(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        tmp.add(Calendar.MONTH, 1);
        if (new Date().getTime() > tmp.getTime().getTime())
            return sdf.format(tmp.getTime());
        else {
            return sdf.format(new Date());
        }
    }

    public String getYear() {
        return "" + date.get(Calendar.YEAR);
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public JalaliCalendar getDate() {
        return date;
    }

    public void setDate(JalaliCalendar date) {
        this.date = date;
    }

    public boolean isActive() {
        return isActive;
    }
}

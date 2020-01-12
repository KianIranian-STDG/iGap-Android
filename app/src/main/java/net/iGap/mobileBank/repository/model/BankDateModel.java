package net.iGap.mobileBank.repository.model;

public class BankDateModel {
    private String monthName;
    private String monthNum;
    private String year;
    private boolean isSelected;

    public BankDateModel(String monthName, String monthNum, String year, boolean isSelected) {
        this.monthName = monthName;
        this.monthNum = monthNum;
        this.year = year;
        this.isSelected = isSelected;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getMonthNum() {
        return monthNum;
    }

    public void setMonthNum(String monthNum) {
        this.monthNum = monthNum;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

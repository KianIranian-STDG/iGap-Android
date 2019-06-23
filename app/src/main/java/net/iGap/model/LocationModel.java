package net.iGap.model;

public class LocationModel {
    private int countryCode;
    private String countryName;
    private String phoneMask;

    public LocationModel(int countryCode, String countryName, String phoneMask) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.phoneMask = phoneMask;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getPhoneMask() {
        return phoneMask;
    }
}

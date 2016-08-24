package com.iGap.realm;

import io.realm.RealmObject;

/**
 * Created by Erfan on 8/13/2016.
 */
public class
RealmCountry extends RealmObject {
    private String countryName, countryPhonePattern, CountryCode, countryAbbreviation;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryPhonePattern() {
        return countryPhonePattern;
    }

    public void setCountryPhonePattern(String countryPhonePattern) {
        this.countryPhonePattern = countryPhonePattern;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getCountryAbbreviation() {
        return countryAbbreviation;
    }

    public void setCountryAbbreviation(String countryAbbreviation) {
        this.countryAbbreviation = countryAbbreviation;
    }
}

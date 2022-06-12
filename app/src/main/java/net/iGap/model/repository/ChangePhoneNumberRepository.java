package net.iGap.model.repository;

import net.iGap.model.LocationModel;
import net.iGap.observers.interfaces.OnInfoCountryResponse;
import net.iGap.observers.interfaces.OnReceiveInfoLocation;

import net.iGap.request.RequestInfoCountry;
import net.iGap.request.RequestInfoLocation;

public class ChangePhoneNumberRepository {

    private static ChangePhoneNumberRepository instance;
    private String phoneNumber;
    private String regex = "^9\\d{9}$";
    private int callingCode;
    private String isoCode = "IR";
    private String countryName = "";
    private String pattern = "";
    private int infoRetryCount;
    private String regexFetchCodeVerification;
    private String countryCode;
    private long resendDelayTime;

    private ChangePhoneNumberRepository() {
        getInfoLocation();
    }

    public synchronized static ChangePhoneNumberRepository getInstance() {
        if (instance == null) {
            instance = new ChangePhoneNumberRepository();
        }
        return instance;
    }

    public long getResendDelayTime() {
        if (resendDelayTime <= 0) {
            resendDelayTime = 60;
        }
        return resendDelayTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPattern() {
        return pattern;
    }

    public String getRegex() {
        return regex;
    }

    public String getRegexFetchCodeVerification() {
        return regexFetchCodeVerification;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void getInfoLocation() {
        new RequestInfoLocation().infoLocation(new OnReceiveInfoLocation() {
            @Override
            public void onReceive(String isoCodeR, int callingCodeR, String countryNameR, String patternR, String regexR) {
                isoCode = isoCodeR;
                callingCode = callingCodeR;
                countryName = countryNameR;
                pattern = patternR;
                regex = regexR;
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                if (infoRetryCount < 3) {
                    infoRetryCount++;
                    getInfoLocation();
                }
            }
        });
    }

    public void getCountryInfo(String countryAbbreviation, ChangePhoneNumberRepository.RepositoryCallback<LocationModel> callback) {
        new RequestInfoCountry().infoCountry(countryAbbreviation, new OnInfoCountryResponse() {
            @Override
            public void onInfoCountryResponse(int callingCode, String name, String pattern, String regexR) {
                regex = regexR;
                ChangePhoneNumberRepository.this.callingCode = callingCode;
                ChangePhoneNumberRepository.this.countryName = name;
                ChangePhoneNumberRepository.this.pattern = pattern;
                callback.onSuccess(new LocationModel(callingCode, name, pattern));
            }
            @Override
            public void onError(int majorCode, int minorCode) {
                callback.onError();
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError();
    }
}

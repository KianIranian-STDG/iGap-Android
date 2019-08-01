package net.iGap.api.errorhandler;

import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.GsonBuilder;

import javax.net.ssl.SSLHandshakeException;

public class ErrorHandler {

    public ErrorModel getError(int responseCode, String error) {
        if (error != null) {
            if (responseCode < 501) {
                return new GsonBuilder().create().fromJson(error, ErrorModel.class);
            } else {
                return new ErrorModel("502 error", "server Error");
            }
        } else {
            return new ErrorModel("empty error", "error message is null");
        }
    }

    public boolean checkHandShakeFailure(Throwable throwable){
        return throwable instanceof SSLHandshakeException;
    }
}
package net.iGap.api.errorhandler;

import com.google.gson.GsonBuilder;

import net.iGap.G;
import net.iGap.R;

import javax.net.ssl.SSLHandshakeException;

public class ErrorHandler {

    public ErrorModel getError(int responseCode, String error) {

        if (responseCode == 401) {
            return new ErrorModel("001", "Expired Token", true);
        }
        if (error != null) {
            if (responseCode < 501) {
                return new GsonBuilder().create().fromJson(error, ErrorModel.class);
            } else {
                return new ErrorModel("502 error", G.context.getString(R.string.server_error));
            }
        } else {
            return new ErrorModel("empty error", "error message is null");
        }
    }

    public boolean checkHandShakeFailure(Throwable throwable) {
        return throwable instanceof SSLHandshakeException;
    }
}

package net.iGap.helper;

import com.google.gson.Gson;

import net.iGap.api.errorhandler.ErrorModel;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLPeerUnverifiedException;

import retrofit2.HttpException;

public class ExceptionMessageFactory {

    public static ErrorModel getMessage(Throwable throwable) {
        ErrorModel errorResponse = new ErrorModel();
        errorResponse.setThrowable(throwable);

        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;

            if (httpException.code() >= 500) {
                errorResponse.setMessage("خطای داخلی سرور");
            } else {
                if (httpException.response() != null && httpException.response().errorBody() != null) {
                    try {
                        String errorBody = new String(httpException.response().errorBody().bytes());
                        errorResponse = new Gson().fromJson(errorBody, ErrorModel.class);
                        return errorResponse;
                    } catch (IOException e) {
                        errorResponse.setMessage("خطای نامشخص");
                        e.printStackTrace();
                    } catch (Exception e) {
                        errorResponse.setMessage("خطای نامشخص");
                        e.printStackTrace();
                    }
                }
            }
        } else if (throwable instanceof SocketTimeoutException) {
            errorResponse.setMessage("اتمام زمان درخواست");
        } else if (throwable instanceof SSLPeerUnverifiedException) {
            errorResponse.setMessage("خطای مجوز (نرم افزار را بروزرسانی کنید!)");
        } else {
            errorResponse.setMessage("خطای نامشخص");
        }

        HelperLog.getInstance().setErrorLog(new Exception(throwable.getMessage()));

        return errorResponse;
    }
}

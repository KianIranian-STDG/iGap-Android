package net.iGap.api.apiService;

import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperLog;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileBankApiInitializer<T> {

    public void initAPI(@NotNull Call<T> retrofitCall, MobileBankExpiredTokenCallback expiredToken, ResponseCallback<T> retrofitCallback) {

        retrofitCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
                if (response.isSuccessful())
                    retrofitCallback.onSuccess(response.body());
                else {
                    if (response.code() == 400) { //must be 401
                        expiredToken.onExpired();
                    } else {
                        try {
                            ErrorModel error = new ErrorHandler().getError(response.code(), response.errorBody().string());
                            retrofitCallback.onError(error.getMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                            retrofitCallback.onFailed();
                            HelperLog.setErrorLog(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                            retrofitCallback.onFailed();
                            HelperLog.setErrorLog(e);
                            HelperLog.setErrorLog(new Exception(retrofitCall.request().url().toString()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<T> call, @NotNull Throwable t) {
                t.printStackTrace();
                retrofitCallback.onFailed();
            }
        });

    }
}

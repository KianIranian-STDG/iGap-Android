package net.iGap.api.apiService;

import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperLog;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.OnRefreshToken;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.request.RequestUserRefreshToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiInitializer<T> {

    public void initAPI(Call<T> retrofitCall, HandShakeCallback handShakeCallback, ResponseCallback<T> retrofitCallback) {

        retrofitCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
                if (response.isSuccessful())
                    retrofitCallback.onSuccess(response.body());
                else {
                    try {
                        ErrorModel error = new ErrorHandler().getError(response.code(), response.errorBody().string());
                        if (error.getName().equals("001") && error.isNeedToRefresh()) {
                            new RequestUserRefreshToken().RefreshUserToken(new OnRefreshToken() {
                                @Override
                                public void onRefreshToken(String token) {
                                    initAPI(retrofitCall.clone(), handShakeCallback, retrofitCallback);
                                    HelperLog.getInstance().setErrorLog(new Exception("update jwt token from ApiInitializer"));
                                }

                                @Override
                                public void onError(int majorCode, int minorCode) {
                                    retrofitCallback.onFailed();
                                }
                            });
                            return;
                        }
                        retrofitCallback.onError(error.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        retrofitCallback.onFailed();
                        HelperLog.getInstance().setErrorLog(e);
                    } catch (Exception e) {
                        e.printStackTrace();
                        retrofitCallback.onFailed();
                        HelperLog.getInstance().setErrorLog(e);
                        HelperLog.getInstance().setErrorLog(new Exception(retrofitCall.request().url().toString()));
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<T> call, @NotNull Throwable t) {
                t.printStackTrace();
                if (new ErrorHandler().checkHandShakeFailure(t)) {
                    if (handShakeCallback != null)
                        handShakeCallback.onHandShake();
                } else {
                    retrofitCallback.onFailed();
                }
            }
        });

    }
}

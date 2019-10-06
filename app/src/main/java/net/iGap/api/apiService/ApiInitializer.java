package net.iGap.api.apiService;

import android.util.Log;

import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.interfaces.OnRefreshToken;
import net.iGap.request.RequestUserRefreshToken;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiInitializer<T> {

    public void initAPI (Call<T> retrofitCall, HandShakeCallback handShakeCallback, ResponseCallback<T> retrofitCallback) {

        retrofitCallback.setProgressIndicator(true);
        retrofitCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                Log.d("amini", "onResponse: " + response.isSuccessful() + " " + response.code());
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
                                }

                                @Override
                                public void onError(int majorCode, int minorCode) {
                                    retrofitCallback.onError(new ErrorModel("", "Something went wrong. Please try again later."));
                                    retrofitCallback.onError("Something went wrong. Please try again later.");
                                }
                            });
                            return;
                        }
                        retrofitCallback.onError(error);
                        retrofitCallback.onError(error.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        retrofitCallback.onError(new ErrorModel("", "Something went wrong. Please try again later."));
                        retrofitCallback.onError("Something went wrong. Please try again later.");
                    }
                }
                retrofitCallback.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Log.d("amini", "onResponse: " + t.getMessage());
                t.printStackTrace();
                retrofitCallback.setProgressIndicator(false);
                retrofitCallback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
                if (new ErrorHandler().checkHandShakeFailure(t)) {
                    handShakeCallback.onHandShake();
                    return;
                }
                retrofitCallback.onError("Server is NOT responding.");
            }
        });

    }

    public void initAPI (Call<T> retrofitCall, ResponseCallback<T> retrofitCallback) {

        retrofitCallback.setProgressIndicator(true);
        retrofitCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                Log.d("amini", "onResponse: " + response.isSuccessful() + " " + response.code());
                if (response.isSuccessful())
                    retrofitCallback.onSuccess(response.body());
                else {
                    try {
                        ErrorModel error = new ErrorHandler().getError(response.code(), response.errorBody().string());
                        if (error.getName().equals("001") && error.isNeedToRefresh()) {
                            new RequestUserRefreshToken().RefreshUserToken(new OnRefreshToken() {
                                @Override
                                public void onRefreshToken(String token) {
                                    initAPI(retrofitCall.clone(), retrofitCallback);
                                }

                                @Override
                                public void onError(int majorCode, int minorCode) {
                                    retrofitCallback.onError(new ErrorModel("", "Something went wrong. Please try again later."));
                                    retrofitCallback.onError("Something went wrong. Please try again later.");
                                }
                            });
                            return;
                        }
                        retrofitCallback.onError(error);
                        retrofitCallback.onError(error.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        retrofitCallback.onError(new ErrorModel("", "Something went wrong. Please try again later."));
                        retrofitCallback.onError("Something went wrong. Please try again later.");
                    }
                }
                retrofitCallback.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Log.d("amini", "onResponse: " + t.getMessage());
                t.printStackTrace();
                retrofitCallback.setProgressIndicator(false);
                retrofitCallback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
                if (new ErrorHandler().checkHandShakeFailure(t)) {
                    retrofitCallback.onHandShake();
                    return;
                }
                retrofitCallback.onError("Server is NOT responding.");
            }
        });

    }

}

package net.iGap.payment;

import androidx.annotation.NonNull;

import net.iGap.api.PaymentApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.api.apiService.ResponseCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRepository {

    private static PaymentRepository instance;

    private PaymentApi paymentApi;

    public static PaymentRepository getInstance() {
        if (instance == null) {
            instance = new PaymentRepository();
        }
        return instance;
    }

    public void clearRepository() {
        instance = null;
    }

    private PaymentRepository() {
        paymentApi = new RetrofitFactory().getPaymentRetrofit().create(PaymentApi.class);
    }

    public void checkOrder(String orderToken, ResponseCallback<CheckOrderResponse> callBack) {
        paymentApi.requestCheckOrder(orderToken).enqueue(new Callback<CheckOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckOrderResponse> call, @NonNull Response<CheckOrderResponse> response) {
                if (response.code() == 200) {
                    callBack.onSuccess(response.body());
                } else {
                    try {
                        callBack.onError(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckOrderResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                callBack.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }

    public void checkOrderStatus(String orderId, ResponseCallback<CheckOrderStatusResponse> callback) {
        paymentApi.requestCheckOrderStatus(orderId).enqueue(new Callback<CheckOrderStatusResponse>() {
            @Override
            public void onResponse(@NotNull Call<CheckOrderStatusResponse> call, @NotNull Response<CheckOrderStatusResponse> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<CheckOrderStatusResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }
}

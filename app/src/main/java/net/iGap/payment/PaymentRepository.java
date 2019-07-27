package net.iGap.payment;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import net.iGap.api.PaymentApi;
import net.iGap.api.apiService.RetrofitFactory;

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

    public static void clearRepository() {
        instance = null;
    }

    private PaymentRepository() {
        paymentApi = new RetrofitFactory().getPaymentRetrofit().create(PaymentApi.class);
    }

    public void checkOrder(String orderToken, PaymentCallback callBack) {
        paymentApi.requestCheckOrder(orderToken).enqueue(new Callback<CheckOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<CheckOrderResponse> call, @NonNull Response<CheckOrderResponse> response) {
                if (response.code() == 200) {
                    callBack.onSuccess(response.body());
                } else {
                    try {
                        callBack.onError(getError(response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckOrderResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                callBack.onFail();
            }
        });
    }

    private ErrorModel getError(String error){
        return new Gson().fromJson(error,ErrorModel.class);
    }

    public interface PaymentCallback {
        void onSuccess(CheckOrderResponse data);

        void onError(ErrorModel error);

        void onFail();
    }
}

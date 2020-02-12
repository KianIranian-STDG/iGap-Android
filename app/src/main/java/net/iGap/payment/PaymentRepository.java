package net.iGap.payment;

import net.iGap.api.PaymentApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;

public class PaymentRepository {

    private static PaymentRepository instance;

    private PaymentApi paymentApi;

    public static PaymentRepository getInstance() {
        if (instance == null) {
            instance = new PaymentRepository();
        }
        return instance;
    }

    void clearRepository() {
        instance = null;
    }

    private PaymentRepository() {
        paymentApi = new RetrofitFactory().getPaymentRetrofit();
    }

    void checkOrder(String orderToken, HandShakeCallback handShakeCallback, ResponseCallback<CheckOrderResponse> callBack) {
        new ApiInitializer<CheckOrderResponse>().initAPI(paymentApi.requestCheckOrder(orderToken), handShakeCallback, callBack);
    }

    void checkOrderStatus(String orderId, HandShakeCallback handShakeCallback, ResponseCallback<CheckOrderStatusResponse> callback) {

        new ApiInitializer<CheckOrderStatusResponse>().initAPI(paymentApi.requestCheckOrderStatus(orderId), handShakeCallback, callback);

    }
}

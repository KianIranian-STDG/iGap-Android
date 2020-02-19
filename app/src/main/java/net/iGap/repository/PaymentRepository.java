package net.iGap.repository;

import net.iGap.api.PaymentApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.model.payment.CheckOrderResponse;
import net.iGap.model.payment.CheckOrderStatusResponse;
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

    public void clearRepository() {
        instance = null;
    }

    private PaymentRepository() {
        paymentApi = new RetrofitFactory().getPaymentRetrofit();
    }

    public void checkOrder(String orderToken, HandShakeCallback handShakeCallback, ResponseCallback<CheckOrderResponse> callBack) {
        new ApiInitializer<CheckOrderResponse>().initAPI(paymentApi.requestCheckOrder(orderToken), handShakeCallback, callBack);
    }

    public void checkOrderStatus(String orderId, HandShakeCallback handShakeCallback, ResponseCallback<CheckOrderStatusResponse> callback) {

        new ApiInitializer<CheckOrderStatusResponse>().initAPI(paymentApi.requestCheckOrderStatus(orderId), handShakeCallback, callback);

    }
}

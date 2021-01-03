package net.iGap.repository;

import net.iGap.api.ChargeApi;
import net.iGap.api.PaymentApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.model.payment.CheckOrderResponse;
import net.iGap.model.payment.CheckOrderStatusResponse;
import net.iGap.model.paymentPackage.Config;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;

import java.util.HashMap;

public class PaymentRepository {

    private static PaymentRepository instance;

    private final PaymentApi paymentApi;

    private final ChargeApi chargeApi;

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
        chargeApi = new RetrofitFactory().getChargeRetrofit();
    }

    public void checkOrder(String orderToken, HandShakeCallback handShakeCallback, ResponseCallback<CheckOrderResponse> callBack) {
        new ApiInitializer<CheckOrderResponse>().initAPI(paymentApi.requestCheckOrder(orderToken), handShakeCallback, callBack);
    }

    public void checkOrderStatus(String orderId, HandShakeCallback handShakeCallback, ResponseCallback<CheckOrderStatusResponse> callback) {

        new ApiInitializer<CheckOrderStatusResponse>().initAPI(paymentApi.requestCheckOrderStatus(orderId), handShakeCallback, callback);

    }

    public void checkOrderForDiscount(String orderToken, HashMap<String, String> coupon, HandShakeCallback handShakeCallback, ResponseCallback<CheckOrderResponse> callBack) {

        new ApiInitializer<CheckOrderResponse>().initAPI(paymentApi.requestCheckOrderForDiscount(orderToken, coupon), handShakeCallback, callBack);

    }

    public void getConfigs(String userToken, HandShakeCallback handShakeCallback, ResponseCallback<Config> callBack) {

        new ApiInitializer<Config>().initAPI(chargeApi.getConfigs(userToken), handShakeCallback, callBack);

    }
}

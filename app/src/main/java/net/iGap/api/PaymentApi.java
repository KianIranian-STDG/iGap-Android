package net.iGap.api;

import net.iGap.payment.CheckOrderResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;

public interface PaymentApi {

    @GET("order-check/{orderToken}")
    Call<CheckOrderResponse> requestCheckOrder(@Field("orderToken") String orderToken);
}

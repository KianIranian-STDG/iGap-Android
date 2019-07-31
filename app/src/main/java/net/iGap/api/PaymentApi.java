package net.iGap.api;

import net.iGap.payment.CheckOrderResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PaymentApi {

    @GET("order-check/{orderToken}")
    Call<CheckOrderResponse> requestCheckOrder(@Path("orderToken") String orderToken);

    @GET("order-status/{orderId}")
    Call<Object> requestCheckOrderStatus(@Path("orderId") String orderId);
}

package net.iGap.api;

import com.google.gson.JsonObject;

import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.igasht.IGashtLocationItem;
import net.iGap.model.igasht.IGashtLocationService;
import net.iGap.model.igasht.IGashtProvince;
import net.iGap.model.igasht.IGashtTicketDetail;
import net.iGap.model.igasht.TicketHistoryListResponse;
import net.iGap.model.igasht.TicketQRCodeResponse;
import net.iGap.model.igasht.purchaseResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IgashtApi {

    @GET("entertainment/province")
    Call<BaseIGashtResponse<IGashtProvince>> requestGetProvinceList();

    @GET("entertainment/location/{provinceId}")
    Call<BaseIGashtResponse<IGashtLocationItem>> requestGetLocationList(@Path("provinceId") int provinceId);

    @GET("entertainment/service/{locationId}")
    Call<BaseIGashtResponse<IGashtLocationService>> requestGetServiceList(@Path("locationId") int locationId);

    @POST("entertainment/purchase")
    Call<purchaseResponse> requestRegisterOrder(@Body JsonObject jsonObject);

    @GET("entertainment/ticket/list")
    Call<TicketHistoryListResponse<IGashtTicketDetail>> requestGetTicketList(@Query("offet") int offset,
                                                                             @Query("limit") int limit);

    @GET("entertainment/ticket/qr-code/{voucher_number}")
    Call<TicketQRCodeResponse> requestGetTicketQRCode(@Path("voucher_number") String voucherNumber);
}

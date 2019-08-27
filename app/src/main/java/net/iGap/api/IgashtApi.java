package net.iGap.api;

import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.igasht.barcodescaner.TicketQRCodeResponse;
import net.iGap.igasht.historylocation.IGashtTicketDetail;
import net.iGap.igasht.historylocation.TicketHistoryListResponse;
import net.iGap.igasht.locationdetail.RegisterTicketResponse;
import net.iGap.igasht.locationdetail.buyticket.IGashtLocationService;
import net.iGap.igasht.locationdetail.buyticket.IGashtOrder;
import net.iGap.igasht.locationlist.IGashtLocationItem;
import net.iGap.igasht.provinceselect.IGashtProvince;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IgashtApi {

    @GET("province")
    Call<BaseIGashtResponse<IGashtProvince>> requestGetProvinceList();

    @GET("location/{provinceId}")
    Call<BaseIGashtResponse<IGashtLocationItem>> requestGetLocationList(@Path("provinceId") int provinceId);

    @GET("service/{locationId}")
    Call<BaseIGashtResponse<IGashtLocationService>> requestGetServiceList(@Path("locationId") int locationId);

    @POST("purchase")
    Call<RegisterTicketResponse> registerOrder(@Body IGashtOrder order);

    @GET("ticket/list")
    Call<TicketHistoryListResponse<IGashtTicketDetail>> requestGetTicketList(@Query("offet") int offset,
                                                                             @Query("limit") int limit);

    @GET("ticket/qr-code/{voucher_number}")
    Call<TicketQRCodeResponse> requestGetTicketQRCode(@Path("voucher_number") String voucherNumber);
}

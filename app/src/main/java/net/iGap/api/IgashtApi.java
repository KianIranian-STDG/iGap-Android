package net.iGap.api;

import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.igasht.locationdetail.buyticket.IGashtLocationService;
import net.iGap.igasht.provinceselect.IGashtProvince;
import net.iGap.igasht.locationlist.IGashtLocationItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IgashtApi {

    @GET("province")
    Call<BaseIGashtResponse<IGashtProvince>> requestGetProvinceList();

    @GET("location/{provinceId}")
    Call<BaseIGashtResponse<IGashtLocationItem>> requestGetLocationList(@Path("provinceId") int provinceId);

    @GET("service/{locationId}")
    Call<BaseIGashtResponse<IGashtLocationService>> requestGetServiceList(@Path("locationId") int locationId);
}

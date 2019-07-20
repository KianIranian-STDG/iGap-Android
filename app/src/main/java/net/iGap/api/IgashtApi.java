package net.iGap.api;

import net.iGap.igasht.ProvinceListResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IgashtApi {

    @GET("province")
    Call<ProvinceListResponse> requestGetProvinceList();
}

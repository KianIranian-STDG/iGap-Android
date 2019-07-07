package net.iGap.api;

import net.iGap.module.api.popularChannel.NormalChannel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PopularChannelApi {

    @GET("channel/category/normal")
    Call<NormalChannel> getNormalChannel(
            @Query("orderBy") String orderBy,
            @Query("sort") String sort
    );
}

package net.iGap.api;


import net.iGap.module.api.popularChannel.ParentChannel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PopularChannelApi {
//    @GET("/category/{categoryId}")
//    Call<ChildChannel> getChildChannel(
//            @Query("page") int page,
//            @Path("categoryId") String path
//    );

    @GET("channel")
    Call<ParentChannel> getParentChannel();
}

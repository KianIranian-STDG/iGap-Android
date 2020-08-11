package net.iGap.api;


import net.iGap.model.popularChannel.ChildChannel;
import net.iGap.model.popularChannel.ParentChannel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FavoriteChannelApi {

    @GET(" ")
    Call<ParentChannel> getFirstPage();

    @GET("category/{categoryId}")
    Call<ChildChannel> getChildChannel(
            @Path("categoryId") String path,
            @Query("start") int start,
            @Query("display") int display
    );
}

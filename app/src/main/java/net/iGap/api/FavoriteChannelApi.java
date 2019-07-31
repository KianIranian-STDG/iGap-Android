package net.iGap.api;


import net.iGap.model.PopularChannel.ChildChannel;
import net.iGap.model.PopularChannel.ParentChannel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FavoriteChannelApi {

    @GET(" ")
    Call<ParentChannel> getParentChannel();

    @GET("category/{categoryId}")
    Call<ChildChannel> getChildChannel(
            @Path("categoryId") String path,
            @Query("page") int page
    );
}

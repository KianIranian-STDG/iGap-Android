package net.iGap.api;

import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApi {

    @GET("group/list")
    Call<NewsGroup> getNewsGroups();

    @GET("title/latest")
    Call<NewsList> getLatestNews(@Query("start") int start, @Query("display") int display);

    @GET("title/hit")
    Call<NewsList> getMHitsNews(@Query("start") int start, @Query("display") int display);

    @GET("title/featured")
    Call<NewsList> getFeaturedNews(@Query("start") int start, @Query("display") int display);

    @GET("title/category/{groupID}/featured")
    Call<NewsList> getFeaturedNewsForGroup(@Path("groupID") int groupID);

    @GET("group/category/{groupID}/list")
    Call<NewsList> getGroupNews(@Path("groupID") int groupID, @Query("start") int start, @Query("display") int display);


}

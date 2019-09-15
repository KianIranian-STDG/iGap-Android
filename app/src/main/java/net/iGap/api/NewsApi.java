package net.iGap.api;

import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.repository.model.NewsPN;
import net.iGap.news.repository.model.NewsPublisher;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApi {

    @GET("news/group/list") //checked
    Call<NewsGroup> getNewsGroups();

    @GET("news/title/latest") //checked
    Call<NewsList> getLatestNews(@Query("start") int start, @Query("display") int display);

    @GET("news/title/hit") //checked
    Call<NewsList> getMHitsNews(@Query("start") int start, @Query("display") int display);

    @GET("news/title/featured") //checked
    Call<NewsList> getFeaturedNews(@Query("start") int start, @Query("display") int display);

    @GET("news/title/category/{groupID}/featured")
    Call<NewsList> getFeaturedNewsForGroup(@Path("groupID") int groupID, @Query("start") int start, @Query("display") int display);

    @GET("news/title/category/{groupID}/urgent")
    Call<NewsList> getErgentNewsForGroup(@Path("groupID") int groupID, @Query("start") int start, @Query("display") int display);

    @GET("news/group/category/{groupID}/list")
    Call<NewsList> getGroupNews(@Path("groupID") int groupID, @Query("start") int start, @Query("display") int display);

    @GET("news/article/{newsID}")
    Call<NewsDetail> getNewsDetail(@Path("newsID") int newsID);

    @GET("news/source/list") //checked
    Call<List<NewsPublisher>> getNewsPublishers(@Query("start") int start, @Query("display") int display);

    @GET("news/source/{publisherID}")
    Call<NewsPN> getPublisherNews(@Path("publisherID") int publisherID, @Query("start") int start, @Query("display") int display);

    @GET("news") //checked
    Call<List<NewsFPList>> getMainPageNews(@Query("per_source") int numOfNewsPerSource);
}

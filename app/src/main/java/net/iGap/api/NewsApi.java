package net.iGap.api;

import net.iGap.news.repository.model.NewsComment;
import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsFirstPage;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.repository.model.NewsPN;
import net.iGap.news.repository.model.NewsPublisher;
import net.iGap.news.repository.model.NewsSubmitComment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApi {

    @GET("getServices/igap/")
    Call<NewsGroup> getNewsGroups(@Query("page") int page, @Query("perpage") int display);

    @GET("getLastNewsList/igap/")
    Call<NewsList> getLatestNews(@Query("page") int page, @Query("perpage") int display);

    @GET("getHitNewsList/igap/")
    Call<NewsList> getMHitsNews(@Query("page") int page, @Query("perpage") int display);

    @GET("getHighlyControversialNewsList/igap/")
    Call<NewsList> getMControversialNews(@Query("page") int page, @Query("perpage") int display);

    @GET("getFeaturedNewsList/igap/")
    Call<NewsList> getFeaturedNews(@Query("page") int page, @Query("perpage") int display);

    @GET("getFeaturedNewsList/igap/") // ?
    Call<NewsList> getFeaturedNewsForGroup(@Query("serviceId") int groupID, @Query("page") int page, @Query("perpage") int display);

    @GET("getUrgentNewsList/igap/")
    Call<NewsList> getErgentNews(@Query("page") int page, @Query("perpage") int display);

    @GET("getUrgentNewsList/igap/")
    Call<NewsList> getErgentNewsForGroup(@Query("serviceId") int groupID, @Query("page") int page, @Query("perpage") int display);

    @GET("getNewsList/igap/")
    Call<NewsList> getGroupNews(@Query("serviceId") int groupID, @Query("page") int page, @Query("perpage") int display);

    @GET("getNews/igap/")
    Call<NewsDetail> getNewsDetail(@Query("articleId") int newsID);

    @GET("getNewsComments/igap/")
    Call<NewsComment> getNewsComment(@Query("articleId") int newsID, @Query("page") int page, @Query("perpage") int display);

    @POST("setComment/igap/")
//    Call<NewsDetail> postNewsComment(@Field("articleid") String newsID, @Field("comment") String comment, @Field("author") String author, @Field("email") String email);
    Call<NewsDetail> postNewsComment(@Body NewsSubmitComment comment);

    @GET("getSources/igap/")
    Call<List<NewsPublisher>> getNewsPublishers(@Query("page") int page, @Query("perpage") int display);

    @GET("getSourceNewsList/igap/")
    Call<NewsPN> getPublisherNews(@Query("srcid") int publisherID, @Query("page") int page, @Query("perpage") int display);

    @GET("getFirstPageData/igap/")
    Call<List<NewsFirstPage>> getMainPageNews();

    @GET("getRelatedNewsList/igap/")
    Call<NewsList> getRelatedNews(@Query("articleId") int newsID, @Query("page") int page, @Query("perpage") int display);
}

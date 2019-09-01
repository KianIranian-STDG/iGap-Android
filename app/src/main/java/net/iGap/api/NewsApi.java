package net.iGap.api;

import net.iGap.news.repository.model.NewsGroup;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NewsApi {

    @GET("group/list")
    Call<NewsGroup> getNewsGroups();


}

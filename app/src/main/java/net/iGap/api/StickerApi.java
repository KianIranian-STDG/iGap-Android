package net.iGap.api;

import net.iGap.fragments.emoji.apiModels.StickerCategorisDataModel;
import net.iGap.fragments.emoji.apiModels.StickerCategoryGroupDataModel;
import net.iGap.fragments.emoji.apiModels.StickerGroupDataModel;
import net.iGap.fragments.emoji.apiModels.StickersDataModel;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StickerApi {

    @GET("category/list")
    Single<StickerCategorisDataModel> getCategories();

    @GET("category/{categoryId}")
    Single<StickerCategoryGroupDataModel> getCategoryStickers(@Path("categoryId") String categoryId);

    @GET("category/{categoryId}")
    Single<StickerCategoryGroupDataModel> getCategoryStickers(@Path("categoryId") String categoryId, @Query("skip") int skip, @Query("limit") int limit);

    @GET("user-list")
    Single<StickerCategoryGroupDataModel> getUserStickersGroup();

    @POST("{groupId}/user-list")
    Completable addStickerGroupToMyStickers(@Path("groupId") String groupId);

    @DELETE("{groupId}/user-list")
    Completable removeStickerGroupFromMyStickers(@Path("groupId") String groupId);

    @GET("{groupId}")
    Single<StickerGroupDataModel> getStickerGroupStickers(@Path("groupId") String groupId);

    @GET("recently-used/list")
    Single<StickersDataModel> getRecentSticker();

    @POST("recently-used/{stickerId}")
    Single<Object> addStickerToRecent(@Path("stickerId") String stickerId);

    @POST("favorite/{stickerId}")
    Completable addStickerToFavorite(@Path("stickerId") String stickerId);

    @GET("favorite/list")
    Single<StickersDataModel> getFavoriteSticker();

}

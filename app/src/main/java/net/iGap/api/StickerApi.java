package net.iGap.api;

import net.iGap.fragments.emoji.apiModels.StickerCategorisDataModel;
import net.iGap.fragments.emoji.apiModels.StickerCategoryGroupDataModel;
import net.iGap.fragments.emoji.apiModels.StickerGroupDataModel;
import net.iGap.fragments.emoji.apiModels.StickersDataModel;

import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StickerApi {

    @GET("/category")
    Single<StickerCategorisDataModel> getCategories();

    @GET("category/{categoryId}")
    Single<StickerCategoryGroupDataModel> getCategoryStickers(@Path("categoryId") String categoryId);

    @GET("stickers/user-list")
    Single<StickerCategoryGroupDataModel> getUserStickersGroup();

    @POST("stickers/{groupId}/user-list")
    Single<Object> addStickerGroupToMyStickers(@Path("groupId") String groupId);

    @DELETE("stickers/{categoryId}/user-list")
    Single<Object> romoveStickerGroupFromMyStickers(@Path("categoryId") String categoryId);

    @GET("{groupId}")
    Single<StickerGroupDataModel> getStickerGroupStickers(@Path("groupId") String groupId);

    @GET("recently-used/list")
    Single<StickersDataModel> getRecentSticker();

    @POST("recently-used/{stickerId}")
    Single<Object> addStickerToRecent(@Path("stickerId") String stickerId);

    @POST("favorite/{stickerId}")
    Single<Object> addStickerToFavorite(@Path("stickerId") String stickerId);

    @GET("favorite/list")
    Single<StickersDataModel> getFavoriteSticker();

}

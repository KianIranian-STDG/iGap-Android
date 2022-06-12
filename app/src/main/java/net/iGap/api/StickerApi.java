package net.iGap.api;

import com.google.gson.JsonObject;

import net.iGap.fragments.emoji.apiModels.CardStatusDataModel;
import net.iGap.fragments.emoji.apiModels.Ids;
import net.iGap.fragments.emoji.apiModels.IssueDataModel;
import net.iGap.fragments.emoji.apiModels.RsaDataModel;
import net.iGap.fragments.emoji.apiModels.SliderDataModel;
import net.iGap.fragments.emoji.apiModels.StickerCategorisDataModel;
import net.iGap.fragments.emoji.apiModels.StickerCategoryGroupDataModel;
import net.iGap.fragments.emoji.apiModels.StickerGroupDataModel;
import net.iGap.fragments.emoji.apiModels.StickerGroupTypesDataModel;
import net.iGap.fragments.emoji.apiModels.StickersDataModel;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StickerApi {

    @GET("category")
    Single<StickerCategorisDataModel> getCategories();

    @GET("category/{categoryId}")
    Single<StickerCategoryGroupDataModel> getCategoryStickers(@Path("categoryId") String categoryId);

    @GET("category/{categoryId}")
    Single<StickerCategoryGroupDataModel> getCategoryStickers(@Path("categoryId") String categoryId, @Query("skip") int skip, @Query("limit") int limit);

    @GET("category/{categoryId}")
    Single<StickerCategoryGroupDataModel> getCategoryStickers(@Path("categoryId") String categoryId, @Query("skip") int skip, @Query("limit") int limit, @Query("type") String type);

    @GET("user-list")
    Single<StickerCategoryGroupDataModel> getUserStickersGroup();

    @POST("user-list/{groupId}")
    Completable addStickerGroupToMyStickers(@Path("groupId") String groupId);

    @POST("user-list/delete")
    Completable removeStickerGroupFromMyStickers(@Body Ids ids);

    @GET("main/{groupId}")
    Single<StickerGroupDataModel> getStickerGroupStickers(@Path("groupId") String groupId);

    @GET("main/configs")
    Single<StickerGroupTypesDataModel> getConfigs();

    @GET("recently-used/list")
    Single<StickersDataModel> getRecentSticker();

    @POST("recently-used/{stickerId}")
    Single<Object> addStickerToRecent(@Path("stickerId") String stickerId);

    @POST("favorite/{stickerId}")
    Completable addStickerToFavorite(@Path("stickerId") String stickerId);

    @GET("favorite/list")
    Single<StickersDataModel> getFavoriteSticker();

    @GET("gift/giftable-list")
    Single<StickerCategoryGroupDataModel> getGiftableStickers();

    @POST("gift/issue/{stickerId}")
    Single<IssueDataModel> addIssue(@Path("stickerId") String stickerId, @Body JsonObject jsonObject);

    @POST("gift/activate/{giftStickerId}")
    Single<RsaDataModel> activeGiftCard(@Path("giftStickerId") String giftStickerId, @Body JsonObject jsonObject);

    @GET("gift/activation-status/{giftStickerId}")
    Single<CardStatusDataModel> giftCardStatus(@Path("giftStickerId") String giftStickerId);

    @POST("gift/card-info/{giftStickerId}")
    Single<RsaDataModel> getCardInfo(@Path("giftStickerId") String giftStickerId, @Body JsonObject jsonObject);

    @GET("gift/forward/{stickerId}/{toUserId}")
    Completable forwardToUser(@Path("stickerId") String stickerId, @Path("toUserId") String toUserId);

    @GET("gift/first-page")
    Single<SliderDataModel> getGiftStickerHomePage();
}

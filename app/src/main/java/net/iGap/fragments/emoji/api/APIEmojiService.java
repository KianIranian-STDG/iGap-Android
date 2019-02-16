package net.iGap.fragments.emoji.api;

import com.vanniktech.emoji.sticker.struct.StructSticker;

import net.iGap.fragments.emoji.struct.StructEachSticker;
import net.iGap.fragments.emoji.struct.StructStickerResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */
public interface APIEmojiService {

    @POST("/stickers/{id}/favorite")
    Call<StructStickerResult> addSticker(@Path("id") String groupId);

    @POST("/stickers/{id}/favorite")
    Call<StructStickerResult> removeSticker(@Path("id") String groupId);

    @GET("/stickers/favorite")
    Call<StructSticker> getFavoritSticker();

    @GET("/stickers")
    Call<StructSticker> getAllSticker(@Query("skip") int skip,
                                      @Query("limit") int limit);

    @GET("/stickers/{id}")
    Call<StructEachSticker> getSticker(@Path("id") String groupId);
}

package net.iGap.api;


import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.SearchAlbum;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BeepTunesApi {

    @GET("search/album")
    Call<SearchAlbum> getSearchAlbume(
            @Query("q") String q,
            @Query("page") int page
    );

    @GET("album/tracks")
    Call<AlbumTrack> getAlbumeTrack(
            @Query("id") long id
    );
}

package net.iGap.api;

import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.Albums;
import net.iGap.module.api.beepTunes.Artist;
import net.iGap.module.api.beepTunes.FirstPage;
import net.iGap.module.api.beepTunes.PurchaseList;
import net.iGap.module.api.beepTunes.SearchArtist;
import net.iGap.module.api.beepTunes.SearchTrack;
import net.iGap.module.api.beepTunes.TrackInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BeepTunesApi {

    @GET(" ")
    Call<FirstPage> getFirstPage();

    @GET("search/album")
    Call<Albums> getSearchAlbum(
            @Query("q") String q,
            @Query("page") int page
    );

    @GET("album/tracks")
    Call<AlbumTrack> getAlbumTrack(
            @Query("id") long id
    );

    @GET("album/info")
    Call<Album> getAlbumInfo(
            @Query("id") long id
    );

    @GET("search/track")
    Call<SearchTrack> getSearchTrack(
            @Query("q") String q,
            @Query("page") int page);

    @GET("track/info")
    Call<TrackInfo> getTrackInfo(
            @Query("id") long id
    );

    @GET("artist/albums")
    Call<Albums> getArtistAlbums(
            @Query("id") long id
    );

    @GET("artist/info")
    Call<Artist> getArtistInfo(
            @Query("id") long id
    );

    @GET("search/artist")
    Call<SearchArtist> getSearchArtist(
            @Query("q") String q,
            @Query("page") int page
    );

    @GET("purchase-list")
    Call<PurchaseList> getPurchases();
}

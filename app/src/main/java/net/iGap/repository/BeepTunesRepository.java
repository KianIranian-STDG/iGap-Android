package net.iGap.repository;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.AlbumTrack;
import net.iGap.module.api.beepTunes.Albums;
import net.iGap.module.api.beepTunes.Artist;
import net.iGap.module.api.beepTunes.PurchaseList;
import net.iGap.module.api.beepTunes.SearchArtist;
import net.iGap.module.api.beepTunes.SearchTrack;
import net.iGap.module.api.beepTunes.TrackInfo;

public class BeepTunesRepository {
    private BeepTunesApi apiService = new RetrofitFactory().getBeepTunesRetrofit();

    public void getSearchAlbum(String q, int page, ResponseCallback<Albums> apiResponse) {

        new ApiInitializer<Albums>().initAPI(apiService.getSearchAlbum(q, page), null, apiResponse);

    }

    public void getAlbumTrack(long id, ResponseCallback<AlbumTrack> apiResponse) {

        new ApiInitializer<AlbumTrack>().initAPI(apiService.getAlbumTrack(id), null, apiResponse);

    }

    public void getAlbumInfo(long id, ResponseCallback<Album> apiResponse) {

        new ApiInitializer<Album>().initAPI(apiService.getAlbumInfo(id), null, apiResponse);

    }

    public void getSearchTrack(String q, int page, ResponseCallback<SearchTrack> apiResponse) {

        new ApiInitializer<SearchTrack>().initAPI(apiService.getSearchTrack(q, page), null, apiResponse);

    }

    public void getTrackInfo(long id, ResponseCallback<TrackInfo> apiResponse) {

        new ApiInitializer<TrackInfo>().initAPI(apiService.getTrackInfo(id), null, apiResponse);

    }

    public void getArtistAlbums(long id, ResponseCallback<Albums> apiResponse) {

        new ApiInitializer<Albums>().initAPI(apiService.getArtistAlbums(id), null, apiResponse);

    }

    public void getArtistInfo(long id, ResponseCallback<Artist> apiResponse) {

        new ApiInitializer<Artist>().initAPI(apiService.getArtistInfo(id), null, apiResponse);

    }

    public void getSearchArtist(String q, int page, ResponseCallback<SearchArtist> apiResponse) {

        new ApiInitializer<SearchArtist>().initAPI(apiService.getSearchArtist(q, page), null, apiResponse);

    }

    public void getPurchase(ResponseCallback<PurchaseList> apiResponse) {

        new ApiInitializer<PurchaseList>().initAPI(apiService.getPurchases(), null, apiResponse);

    }
}

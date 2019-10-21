package net.iGap.api.apiService;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.ElecBillApi;
import net.iGap.api.FavoriteChannelApi;
import net.iGap.api.KuknosApi;
import net.iGap.api.KuknosHorizenApi;
import net.iGap.api.NewsApi;

public class ApiServiceProvider {
    private static RetrofitFactory factory = new RetrofitFactory();

    private static BeepTunesApi beepTunesApi;
    private static FavoriteChannelApi channelApi;
    private static KuknosApi kuknosApi;
    private static NewsApi newsApi;
    private static ElecBillApi elecApi;
    private static KuknosHorizenApi kuknosHorizenApi;

    public static BeepTunesApi getBeepTunesClient() {
        if (beepTunesApi == null) {
            beepTunesApi = factory.getBeepTunesRetrofit().create(BeepTunesApi.class);
        }
        return beepTunesApi;
    }

    public static FavoriteChannelApi getChannelApi() {
        if (channelApi == null) {
            channelApi = factory.getChannelRetrofit().create(FavoriteChannelApi.class);
        }
        return channelApi;
    }

    public static KuknosApi getKuknosClient() {
        if (kuknosApi == null) {
            kuknosApi = factory.getKuknosRetrofit().create(KuknosApi.class);
        }
        return kuknosApi;
    }

    public static NewsApi getNewsClient() {
        if (newsApi == null) {
            newsApi = factory.getKuknosRetrofit().create(NewsApi.class);
        }
        return newsApi;
    }

    public static ElecBillApi getElecBillClient() {
        if (elecApi == null) {
            elecApi = factory.getKuknosRetrofit().create(ElecBillApi.class);
        }
        return elecApi;
    }

    //todo clean this comment

    /*public static KuknosHorizenApi getKuknosHorizonClient() {
        if (kuknosHorizenApi == null) {
            kuknosHorizenApi = factory.getKuknosHorizanRetrofit().create(KuknosHorizenApi.class);
        }
        return kuknosHorizenApi;
    }*/

}

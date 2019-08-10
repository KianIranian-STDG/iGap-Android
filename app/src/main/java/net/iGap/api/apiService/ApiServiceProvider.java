package net.iGap.api.apiService;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.IgashtApi;
import net.iGap.api.KuknosApi;
import net.iGap.api.FavoriteChannelApi;

public class ApiServiceProvider {
    private static RetrofitFactory factory = new RetrofitFactory();

    private static BeepTunesApi beepTunesApi;
    private static FavoriteChannelApi channelApi;
    private static KuknosApi kuknosApi;

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
}

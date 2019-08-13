package net.iGap.api.apiService;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.KuknosApi;
import net.iGap.api.KuknosHorizenApi;
import net.iGap.api.PopularChannelApi;

public class ApiServiceProvider {
    private static RetrofitFactory factory = new RetrofitFactory();

    private static BeepTunesApi beepTunesApi;
    private static PopularChannelApi channelApi;
    private static KuknosApi kuknosApi;
    private static KuknosHorizenApi kuknosHorizenApi;

    public static BeepTunesApi getBeepTunesClient() {
        if (beepTunesApi == null) {
            beepTunesApi = factory.getBeepTunesRetrofit().create(BeepTunesApi.class);
        }
        return beepTunesApi;
    }

    public static PopularChannelApi getChannelApi() {
        if (channelApi == null) {
            channelApi = factory.getChannelRetrofit().create(PopularChannelApi.class);
        }
        return channelApi;
    }

    public static KuknosApi getKuknosClient() {
        if (kuknosApi == null) {
            kuknosApi = factory.getKuknosRetrofit().create(KuknosApi.class);
        }
        return kuknosApi;
    }

    //todo clean this comment

    /*public static KuknosHorizenApi getKuknosHorizonClient() {
        if (kuknosHorizenApi == null) {
            kuknosHorizenApi = factory.getKuknosHorizanRetrofit().create(KuknosHorizenApi.class);
        }
        return kuknosHorizenApi;
    }*/

}

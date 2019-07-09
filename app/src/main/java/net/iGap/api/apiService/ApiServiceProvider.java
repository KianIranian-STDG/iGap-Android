package net.iGap.api.apiService;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.PopularChannelApi;

public class ApiServiceProvider {
    private static RetrofitFactory factory = new RetrofitFactory();

    private static BeepTunesApi beepTunesApi;
    private static PopularChannelApi channelApi;

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

}

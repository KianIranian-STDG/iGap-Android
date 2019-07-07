package net.iGap.api.apiService;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.ChannelApi;

public class ApiServiceProvider {
    private static RetrofitFactory factory = new RetrofitFactory();

    private static BeepTunesApi beepTunesApi;
    private static ChannelApi channelApi;

    public static BeepTunesApi getBeepTunesClient() {
        if (beepTunesApi == null) {
            beepTunesApi = factory.getRetrofit(ApiStatic.BEEP_TUNES).create(BeepTunesApi.class);
        }
        return beepTunesApi;
    }

    public static ChannelApi getChannelApi() {
        if (channelApi == null) {
            channelApi = factory.getRetrofit(ApiStatic.CHANNEL).create(ChannelApi.class);
        }
        return channelApi;
    }

}

package net.iGap.viewmodel.repository;

import android.util.Log;

import net.iGap.api.FavoriteChannelApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.model.popularChannel.ChildChannel;
import net.iGap.model.popularChannel.ParentChannel;

public class PopularChannelRepository {

    private static PopularChannelRepository repository;

    private FavoriteChannelApi channelApi;

    public static PopularChannelRepository getInstance() {
        if (repository == null) {
            repository = new PopularChannelRepository();
        }
        return repository;
    }

    public static void clearInstance() {
        Log.wtf(PopularChannelRepository.class.getName(), "clearInstance");
        repository = null;
    }

    private PopularChannelRepository() {
        channelApi = new RetrofitFactory().getChannelRetrofit();
    }

    public void getFirstPage(HandShakeCallback handShakeCallback, ResponseCallback<ParentChannel> callback) {
        new ApiInitializer<ParentChannel>().initAPI(channelApi.getFirstPage(), handShakeCallback, callback);
    }

    public void getChildChannel(String id, int start, int count, HandShakeCallback handShakeCallback, ResponseCallback<ChildChannel> callback) {
        new ApiInitializer<ChildChannel>().initAPI(channelApi.getChildChannel(id, start, count), handShakeCallback, callback);
    }
}

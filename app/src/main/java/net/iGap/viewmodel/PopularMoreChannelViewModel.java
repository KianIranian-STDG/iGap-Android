package net.iGap.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.FavoriteChannelApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperUrl;
import net.iGap.model.popularChannel.Channel;
import net.iGap.model.popularChannel.ChildChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularMoreChannelViewModel extends BaseViewModel {
    private FavoriteChannelApi channelApi = ApiServiceProvider.getChannelApi();

    private MutableLiveData<ChildChannel> moreChannelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> emptyViewMutableLiveData = new MutableLiveData<>();

    @Override
    public void onCreateViewModel() {
        super.onCreateViewModel();
    }

    public void getFirstPage(String id, int start, int end) {
        progressMutableLiveData.postValue(true);
        new ApiInitializer<ChildChannel>().initAPI(channelApi.getChildChannel(id, start, end), this, new ResponseCallback<ChildChannel>() {
            @Override
            public void onSuccess(ChildChannel data) {
                progressMutableLiveData.postValue(false);
                moreChannelMutableLiveData.postValue(data);

                if (data.getChannels() != null)
                    if (data.getChannels().size() > 0)
                        emptyViewMutableLiveData.postValue(false);
                    else
                        emptyViewMutableLiveData.postValue(true);
            }

            @Override
            public void onError(ErrorModel error) {
                progressMutableLiveData.postValue(false);
            }
        });
    }

    public void onSlideClick(BaseFragment fragment, ChildChannel childChannel, int position) {
        if (childChannel.getInfo().getAdvertisement().getSlides().get(position).getActionType() == 3) {
            HelperUrl.checkUsernameAndGoToRoom(fragment.getActivity(), childChannel.getInfo()
                    .getAdvertisement().getSlides().get(position).getmActionLink(), HelperUrl.ChatEntry.chat);
        }
    }

    public void onChannelClick(Channel channel, BaseFragment fragment) {
        if (channel.getmType().equals(Channel.TYPE_PRIVATE))
            HelperUrl.checkAndJoinToRoom(fragment.getActivity(), channel.getSlug());
        if (channel.getmType().equals(Channel.TYPE_PUBLIC))
            HelperUrl.checkUsernameAndGoToRoom(fragment.getActivity(), channel.getSlug(), HelperUrl.ChatEntry.chat);
    }

    public MutableLiveData<ChildChannel> getMoreChannelMutableLiveData() {
        return moreChannelMutableLiveData;
    }

    public MutableLiveData<Boolean> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

    public MutableLiveData<Boolean> getEmptyViewMutableLiveData() {
        return emptyViewMutableLiveData;
    }
}

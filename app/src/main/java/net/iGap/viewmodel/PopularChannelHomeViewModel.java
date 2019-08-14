package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.support.v4.app.FragmentTransaction;

import net.iGap.R;
import net.iGap.api.FavoriteChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.beepTunes.main.SliderBannerImageLoadingService;
import net.iGap.fragments.populaChannel.PopularChannelHomeFragment;
import net.iGap.fragments.populaChannel.PopularMoreChannelFragment;
import net.iGap.helper.HelperUrl;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.model.popularChannel.Channel;
import net.iGap.model.popularChannel.ParentChannel;
import net.iGap.viewmodel.BaseViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularChannelHomeViewModel extends BaseViewModel {
    private FavoriteChannelApi channelApi = ApiServiceProvider.getChannelApi();

    private MutableLiveData<ParentChannel> firstPageMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressMutableLiveData = new MutableLiveData<>();


    @Override
    public void onCreateViewModel() {
        super.onCreateViewModel();
        BannerSlider.init(new SliderBannerImageLoadingService());
    }

    @Override
    public void onStartFragment(BaseFragment fragment) {
        getFirstPage();
    }

    public void getFirstPage() {
        progressMutableLiveData.postValue(true);
        channelApi.getFirstPage().enqueue(new Callback<ParentChannel>() {
            @Override
            public void onResponse(Call<ParentChannel> call, Response<ParentChannel> response) {
                progressMutableLiveData.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    firstPageMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ParentChannel> call, Throwable t) {
                progressMutableLiveData.postValue(false);
            }
        });
    }

    public void onSlideClick(int position) {

    }

    public void onChannelClick(Channel channel, BaseFragment fragment) {
        if (channel.getmType().equals(Channel.TYPE_PRIVATE))
            HelperUrl.checkAndJoinToRoom(fragment.getActivity(), channel.getSlug());
        if (channel.getmType().equals(Channel.TYPE_PUBLIC))
            HelperUrl.checkUsernameAndGoToRoom(fragment.getActivity(), channel.getSlug(), HelperUrl.ChatEntry.chat);
    }

    public void onMoreClick(String moreId, String title, PopularChannelHomeFragment fragment) {
        PopularMoreChannelFragment moreChannelFragment = new PopularMoreChannelFragment();
        moreChannelFragment.setId(moreId);
        moreChannelFragment.setTitle(title);
        FragmentTransaction fragmentTransition = fragment.getFragmentManager().beginTransaction();
        fragmentTransition.replace(R.id.popularChannel_container, moreChannelFragment);
        fragmentTransition.addToBackStack(null);
        fragmentTransition.commit();
    }

    public MutableLiveData<ParentChannel> getFirstPageMutableLiveData() {
        return firstPageMutableLiveData;
    }

    public MutableLiveData<Boolean> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }
}

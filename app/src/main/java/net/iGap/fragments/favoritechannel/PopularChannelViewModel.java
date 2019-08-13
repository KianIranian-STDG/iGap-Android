package net.iGap.fragments.favoritechannel;

import android.arch.lifecycle.MutableLiveData;

import net.iGap.api.FavoriteChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.beepTunes.main.SliderBannerImageLoadingService;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.model.FavoriteChannel.Category;
import net.iGap.model.FavoriteChannel.Channel;
import net.iGap.model.FavoriteChannel.ParentChannel;
import net.iGap.viewmodel.BaseViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularChannelViewModel extends BaseViewModel {
    private FavoriteChannelApi channelApi = ApiServiceProvider.getChannelApi();

    private MutableLiveData<ParentChannel> firstPageMutableLiveData = new MutableLiveData<>();


    @Override
    public void onCreateViewModel() {
        super.onCreateViewModel();
        BannerSlider.init(new SliderBannerImageLoadingService());
    }

    @Override
    public void onStartFragment(BaseFragment fragment) {
        getFirstPage();
    }

    private void getFirstPage() {
        channelApi.getFirstPage().enqueue(new Callback<ParentChannel>() {
            @Override
            public void onResponse(Call<ParentChannel> call, Response<ParentChannel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    firstPageMutableLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ParentChannel> call, Throwable t) {

            }
        });
    }

    public void onSlideClick(int position) {

    }

    public void onChannelClick(Channel channel) {

    }

    public void onCategoryClick(Category category) {

    }

    public MutableLiveData<ParentChannel> getFirstPageMutableLiveData() {
        return firstPageMutableLiveData;
    }
}

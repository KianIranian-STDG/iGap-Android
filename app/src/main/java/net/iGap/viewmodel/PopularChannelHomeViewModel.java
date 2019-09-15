package net.iGap.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.FavoriteChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentWebView;
import net.iGap.fragments.beepTunes.main.SliderBannerImageLoadingService;
import net.iGap.fragments.populaChannel.PopularChannelHomeFragment;
import net.iGap.fragments.populaChannel.PopularMoreChannelFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperUrl;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.model.popularChannel.Channel;
import net.iGap.model.popularChannel.ParentChannel;
import net.iGap.model.popularChannel.Slide;
import net.iGap.module.SHP_SETTING;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularChannelHomeViewModel extends BaseViewModel {
    private FavoriteChannelApi channelApi = ApiServiceProvider.getChannelApi();

    private MutableLiveData<ParentChannel> firstPageMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> emptyViewMutableLiveData = new MutableLiveData<>();


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
        emptyViewMutableLiveData.postValue(View.GONE);
        channelApi.getFirstPage().enqueue(new Callback<ParentChannel>() {
            @Override
            public void onResponse(Call<ParentChannel> call, Response<ParentChannel> response) {
                progressMutableLiveData.postValue(false);
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        firstPageMutableLiveData.postValue(response.body());
                        emptyViewMutableLiveData.postValue(View.GONE);
                    } else {
                        if (response.code() == 401) {
                            HelperLog.setErrorLog(new Exception("401 error on popular channel " + G.getApiToken()));
                        }
                        emptyViewMutableLiveData.postValue(View.VISIBLE);
                    }
                } else {
                    emptyViewMutableLiveData.postValue(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ParentChannel> call, Throwable t) {
                progressMutableLiveData.postValue(false);
                emptyViewMutableLiveData.postValue(View.VISIBLE);
            }
        });
    }

    public void onSlideClick(BaseFragment fragment, Slide slide) {
        if (slide.getActionType() == 3) {
            HelperUrl.checkUsernameAndGoToRoom(fragment.getActivity(), slide.getmActionLink(), HelperUrl.ChatEntry.chat);
        } else if (slide.getActionType() == 4) {
            SharedPreferences sharedPreferences = fragment.getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
            int checkedInAppBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
            if (checkedInAppBrowser == 1 && !HelperUrl.isNeedOpenWithoutBrowser(slide.getmActionLink())) {
                HelperUrl.openBrowser(slide.getmActionLink());
            } else {
                HelperUrl.openWithoutBrowser(slide.getmActionLink());
            }
        } else if (slide.getActionType() == 5) {
            new HelperFragment(fragment.getActivity().getSupportFragmentManager(), FragmentWebView.newInstance(slide.getmActionLink(), false, null)).setReplace(false).load();
        } else if (slide.getActionType() == 12) {
            PopularMoreChannelFragment popularMoreChannelFragment = new PopularMoreChannelFragment();
            popularMoreChannelFragment.setId(slide.getmActionLink());
            new HelperFragment(fragment.getFragmentManager(), popularMoreChannelFragment).setResourceContainer(R.id.popularChannel_container).setReplace(false).load();
        }
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

    public MutableLiveData<Integer> getEmptyViewMutableLiveData() {
        return emptyViewMutableLiveData;
    }
}

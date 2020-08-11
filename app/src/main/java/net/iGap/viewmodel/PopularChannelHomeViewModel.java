package net.iGap.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.adapter.items.popularChannel.PopularChannelHomeAdapter;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.fragments.beepTunes.main.SliderBannerImageLoadingService;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.model.popularChannel.Category;
import net.iGap.model.popularChannel.Channel;
import net.iGap.model.popularChannel.GoToChannel;
import net.iGap.model.popularChannel.ParentChannel;
import net.iGap.model.popularChannel.Slide;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.viewmodel.repository.PopularChannelRepository;

public class PopularChannelHomeViewModel extends BaseAPIViewModel {
    private PopularChannelRepository repository;
    private PopularChannelHomeAdapter.OnFavoriteChannelCallBack recyclerItemClick;

    private MutableLiveData<ParentChannel> firstPageMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> emptyViewMutableLiveData = new MutableLiveData<>();
    private SingleLiveEvent<Category> goToMorePage = new SingleLiveEvent<>();
    private SingleLiveEvent<String> goToRoom = new SingleLiveEvent<>();
    private SingleLiveEvent<GoToChannel> goToChannel = new SingleLiveEvent<>();
    private SingleLiveEvent<String> goToWebViewPage = new SingleLiveEvent<>();
    private SingleLiveEvent<String> openBrowser = new SingleLiveEvent<>();

    public PopularChannelHomeViewModel() {
        repository = PopularChannelRepository.getInstance();
        recyclerItemClick = new PopularChannelHomeAdapter.OnFavoriteChannelCallBack() {
            @Override
            public void onCategoryClick(Category category) {
                goToMorePage.setValue(category);
            }

            @Override
            public void onChannelClick(Channel channel) {
                goToChannel.setValue(new GoToChannel(channel.getSlug(), channel.getmType().equals(Channel.TYPE_PRIVATE)));

            }

            @Override
            public void onSlideClick(Slide slide) {
                if (slide.getActionType() == 3) {
                    goToRoom.setValue(slide.getmActionLink());
                } else if (slide.getActionType() == 4) {
                    openBrowser.setValue(slide.getmActionLink());
                } else if (slide.getActionType() == 5) {
                    goToWebViewPage.setValue(slide.getmActionLink());
                } else if (slide.getActionType() == 12) {
                    Category category = new Category();
                    category.setId(slide.getmActionLink());
                    goToMorePage.setValue(category);
                }
            }

            @Override
            public void onMoreClick(String moreId, String title) {
                Category category = new Category();
                category.setId(moreId);
                category.setTitle(title);
                goToMorePage.setValue(category);
            }
        };
        getFirstPage();
        BannerSlider.init(new SliderBannerImageLoadingService());
    }

    public void getFirstPage() {
        progressMutableLiveData.postValue(true);
        emptyViewMutableLiveData.postValue(View.GONE);
        repository.getFirstPage(this, new ResponseCallback<ParentChannel>() {
            @Override
            public void onSuccess(ParentChannel data) {
                progressMutableLiveData.postValue(false);
                firstPageMutableLiveData.postValue(data);
                emptyViewMutableLiveData.postValue(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressMutableLiveData.postValue(false);
                emptyViewMutableLiveData.postValue(View.VISIBLE);
            }

            @Override
            public void onFailed() {
                progressMutableLiveData.postValue(false);
                emptyViewMutableLiveData.postValue(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onCleared() {
        PopularChannelRepository.clearInstance();
        super.onCleared();
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

    public SingleLiveEvent<Category> getGoToMorePage() {
        return goToMorePage;
    }

    public SingleLiveEvent<String> getGoToRoom() {
        return goToRoom;
    }

    public SingleLiveEvent<GoToChannel> getGoToChannel() {
        return goToChannel;
    }

    public SingleLiveEvent<String> getGoToWebViewPage() {
        return goToWebViewPage;
    }

    public SingleLiveEvent<String> getOpenBrowser() {
        return openBrowser;
    }

    public PopularChannelHomeAdapter.OnFavoriteChannelCallBack getRecyclerItemClick() {
        return recyclerItemClick;
    }
}

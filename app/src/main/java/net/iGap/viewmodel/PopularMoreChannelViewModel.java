package net.iGap.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.model.popularChannel.Advertisement;
import net.iGap.model.popularChannel.Channel;
import net.iGap.model.popularChannel.ChildChannel;
import net.iGap.model.popularChannel.Slide;

import java.util.ArrayList;
import java.util.List;

public class PopularMoreChannelViewModel extends BaseViewModel {

    private MutableLiveData<List<Channel>> moreChannelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Advertisement> showAdvertisement = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> emptyViewMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> showRetryView = new MutableLiveData<>();
    private MutableLiveData<String> showErrorMessage = new MutableLiveData<>();

    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    private MutableLiveData<goToChannel> goToChannel = new MutableLiveData<>();
    private MutableLiveData<String> toolbarTitle = new MutableLiveData<>();

    private final int pageMax = 20;
    private int totalItemSize = 0;
    private List<Channel> items;
    private boolean isLoadMore = false;
    private int page = 0;
    private String id;
    private String title = "";
    private String scale;

    public PopularMoreChannelViewModel(String id, String title) {
        this.id = id;
        this.title = title;
        items = new ArrayList<>();
        if (id == null) {
            goBack.setValue(true);
        } else {
            getFirstPage();
        }
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public MutableLiveData<PopularMoreChannelViewModel.goToChannel> getGoToChannel() {
        return goToChannel;
    }

    public MutableLiveData<String> getToolbarTitle() {
        return toolbarTitle;
    }

    public MutableLiveData<Advertisement> getShowAdvertisement() {
        return showAdvertisement;
    }

    private void getFirstPage() {
        isLoadMore = true;
        progressMutableLiveData.postValue(true);
        showRetryView.setValue(false);
        new ApiInitializer<ChildChannel>().initAPI(ApiServiceProvider.getChannelApi().getChildChannel(id, pageMax * page, pageMax), this, new ResponseCallback<ChildChannel>() {
            @Override
            public void onSuccess(ChildChannel data) {
                Log.wtf(this.getClass().getName(), "onSuccess");
                if (data != null) {
                    Log.wtf(this.getClass().getName(), "size: " + data.getChannels().size());
                    if (title.equals("")) {
                        if (G.isAppRtl) {
                            title = data.getInfo().getTitle();
                        } else {
                            title = data.getInfo().getTitleEn();
                        }
                        toolbarTitle.setValue(title);
                        Log.wtf(this.getClass().getName(), "set title");
                    }

                    if (data.getInfo().getAdvertisement() != null && data.getInfo().getHasAd()) {
                        Log.wtf(this.getClass().getName(), "Advertisement");
                        showAdvertisement.setValue(data.getInfo().getAdvertisement());
                        scale = data.getInfo().getAdvertisement().getmScale();
                    }
                    items.addAll(data.getChannels());
                    totalItemSize = (int) data.getPagination().getTotalDocs();
                    moreChannelMutableLiveData.setValue(items);
                    progressMutableLiveData.postValue(false);
                    emptyViewMutableLiveData.setValue(items.size() == 0);
                    page++;
                } else {
                    showErrorMessage.setValue("kndnjdfbjf");
                }
                isLoadMore = false;
            }

            @Override
            public void onError(ErrorModel error) {
                progressMutableLiveData.postValue(false);
                if (items.size() == 0) {
                    showRetryView.setValue(true);
                }
            }
        });
    }

    public void onSwipeRefresh() {
        if (!isLoadMore) {
            page = 0;
            totalItemSize = 0;
            items.clear();
            getFirstPage();
        }
    }

    public void toolbarBackClick() {
        goBack.setValue(true);
    }

    public void onSlideClick(Slide slide) {
        if (slide.getActionType() == 3) {
            goToChannel.setValue(new goToChannel(slide.getmActionLink(), false));
        }
    }

    public void onChannelClick(Channel channel) {
        goToChannel.setValue(new goToChannel(channel.getSlug(), channel.getmType().equals(Channel.TYPE_PRIVATE)));
    }

    public void loadMoreData() {
        if (!isLoadMore && totalItemSize > items.size()) {
            getFirstPage();
        }
    }

    public MutableLiveData<List<Channel>> getMoreChannelMutableLiveData() {
        return moreChannelMutableLiveData;
    }

    public MutableLiveData<Boolean> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

    public MutableLiveData<Boolean> getEmptyViewMutableLiveData() {
        return emptyViewMutableLiveData;
    }

    public String getScale() {
        return scale;
    }

    public class goToChannel {
        private String slug;
        private boolean isPrivate;

        public goToChannel(String slug, boolean isPrivate) {
            this.slug = slug;
            this.isPrivate = isPrivate;
        }

        public String getSlug() {
            return slug;
        }

        public boolean isPrivate() {
            return isPrivate;
        }
    }
}

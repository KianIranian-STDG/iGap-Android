package net.iGap.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.model.popularChannel.Advertisement;
import net.iGap.model.popularChannel.Channel;
import net.iGap.model.popularChannel.ChildChannel;
import net.iGap.model.popularChannel.GoToChannel;
import net.iGap.model.popularChannel.Slide;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.viewmodel.repository.PopularChannelRepository;

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
    private MutableLiveData<GoToChannel> goToChannel = new MutableLiveData<>();
    private MutableLiveData<String> toolbarTitle = new MutableLiveData<>();

    private PopularChannelRepository repository;

    private final int pageMax = 20;
    private int totalItemSize = 0;
    private List<Channel> items;
    private boolean isLoadMore = false;
    private int page = 0;
    private String id;
    private String title;
    private String scale;

    public PopularMoreChannelViewModel(String id, String title) {
        this.id = id;
        this.title = title;
        repository = PopularChannelRepository.getInstance();
        items = new ArrayList<>();
        if (id == null) {
            goBack.setValue(true);
        } else {
            getFirstPage(true);
        }
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public MutableLiveData<GoToChannel> getGoToChannel() {
        return goToChannel;
    }

    public MutableLiveData<String> getToolbarTitle() {
        return toolbarTitle;
    }

    public MutableLiveData<Advertisement> getShowAdvertisement() {
        return showAdvertisement;
    }

    private void getFirstPage(boolean isRefresh) {
        isLoadMore = true;
        progressMutableLiveData.postValue(true);
        showRetryView.setValue(false);
        repository.getChildChannel(id, pageMax * page, pageMax, this, new ResponseCallback<ChildChannel>() {
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
                    totalItemSize = (int) data.getPagination().getTotalDocs();
                    if (isRefresh) {
                        items.clear();
                    }
                    items.addAll(data.getChannels());
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
            public void onError(String error) {
                isLoadMore = false;
                progressMutableLiveData.postValue(false);
                if (items.size() == 0) {
                    showRetryView.setValue(true);
                }
            }

            @Override
            public void onFailed() {
                isLoadMore = false;
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
            getFirstPage(true);
        }
    }

    public void toolbarBackClick() {
        goBack.setValue(true);
    }

    public void onSlideClick(Slide slide) {
        if (slide.getActionType() == 3) {
            goToChannel.setValue(new GoToChannel(slide.getmActionLink(), false));
        }
    }

    public void onChannelClick(Channel channel) {
        goToChannel.setValue(new GoToChannel(channel.getSlug(), channel.getmType().equals(Channel.TYPE_PRIVATE)));
    }

    public void loadMoreData() {
        if (!isLoadMore && totalItemSize > items.size()) {
            getFirstPage(false);
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

    public MutableLiveData<Boolean> getShowRetryView() {
        return showRetryView;
    }

    public String getScale() {
        return scale;
    }
}

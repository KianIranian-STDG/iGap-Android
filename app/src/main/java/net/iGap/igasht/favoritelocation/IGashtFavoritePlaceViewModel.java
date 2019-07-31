package net.iGap.igasht.favoritelocation;

import android.arch.lifecycle.MutableLiveData;
import android.view.View;

import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;

import java.util.List;

public class IGashtFavoritePlaceViewModel extends BaseIGashtViewModel<List<String>> {

    private MutableLiveData<List<String>> favoriteList = new MutableLiveData<>();
    private IGashtRepository repository;

    public IGashtFavoritePlaceViewModel() {
        repository = IGashtRepository.getInstance();
        getFavoriteListData();
    }

    public MutableLiveData<List<String>> getFavoriteList() {
        return favoriteList;
    }

    private void getFavoriteListData() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        repository.getFavoriteList(this);
    }

    @Override
    public void onSuccess(List<String> data) {
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        favoriteList.setValue(data);
    }

    public void onRetryClick() {
        getFavoriteListData();
    }

    public void onClickFavoriteItem(int position){

    }
}

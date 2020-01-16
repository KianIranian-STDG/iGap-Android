package net.iGap.fragments.giftStickers;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.module.SingleLiveEvent;
import net.iGap.repository.sticker.StickerRepository;

import java.util.ArrayList;
import java.util.List;

public class MyGiftStickerBuyViewModel extends ViewModel {

    private MutableLiveData<List<String>> loadStickerList = new MutableLiveData<>();
    private SingleLiveEvent<String> showRequestErrorMessage = new SingleLiveEvent<>();
    private ObservableInt showLoading = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableInt showEmptyListMessage = new ObservableInt(View.GONE);

    private StickerRepository repository;
    private List<String> stickerList = new ArrayList<>();

    public MyGiftStickerBuyViewModel(){
        //set request get sticker list

    }


    public void onRetryButtonClick(){

    }

    public ObservableInt getShowLoading() {
        return showLoading;
    }

    public ObservableInt getShowRetryView() {
        return showRetryView;
    }

    public ObservableInt getShowEmptyListMessage() {
        return showEmptyListMessage;
    }

    public MutableLiveData<List<String>> getLoadStickerList() {
        return loadStickerList;
    }

    public SingleLiveEvent<String> getShowRequestErrorMessage() {
        return showRequestErrorMessage;
    }
}

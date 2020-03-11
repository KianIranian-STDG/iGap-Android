package net.iGap.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import net.iGap.module.SingleLiveEvent;

public class FileManagerViewModel extends BaseViewModel {

    private ObservableInt sendBoxVisibility = new ObservableInt(View.GONE);
    private ObservableField<String> selectedCount = new ObservableField<>("1");
    private SingleLiveEvent<Boolean> sendClickListener = new SingleLiveEvent<>();

    public void OnSendClick() {
        sendClickListener.postValue(true);
    }

    public void onCaptionChanged(String caption) {

    }

    public SingleLiveEvent<Boolean> getSendClickListener() {
        return sendClickListener;
    }

    public ObservableInt getSendBoxVisibility() {
        return sendBoxVisibility;
    }

    public ObservableField<String> getSelectedCount() {
        return selectedCount;
    }
}

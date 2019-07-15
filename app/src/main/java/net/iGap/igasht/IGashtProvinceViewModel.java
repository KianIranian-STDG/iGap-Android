package net.iGap.igasht;

import android.arch.lifecycle.ViewModel;
import android.database.Observable;
import android.databinding.ObservableField;

public class IGashtProvinceViewModel extends ViewModel {

    private ObservableField<String> backgroundImageUrl = new ObservableField<>();

    public ObservableField<String> getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void onClearProVinceSearchClick() {

    }

    public void onProvinceTextChange(String inputText) {

    }

    public void onSearchClick() {

    }
}

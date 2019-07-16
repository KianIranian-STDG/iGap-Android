package net.iGap.igasht;

import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class IGashtLocationViewModel extends BaseIGashtViewModel {

    private MutableLiveData<List<String>> locationList = new MutableLiveData<>();

    public MutableLiveData<List<String>> getLocationList() {
        return locationList;
    }

    public IGashtLocationViewModel() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        new Handler().postDelayed(() -> {
            ArrayList<String> tmp = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                tmp.add("item " + i);
            }
            locationList.postValue(tmp);
        }, 2000);
    }

    public void onRetryClick() {

    }
}

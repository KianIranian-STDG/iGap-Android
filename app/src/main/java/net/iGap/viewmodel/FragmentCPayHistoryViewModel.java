package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class FragmentCPayHistoryViewModel extends ViewModel {

    private MutableLiveData<Integer> onFiltersButtonStateChangeListener = new MutableLiveData<>();

    public FragmentCPayHistoryViewModel() {
    }

    public void onFilterClicked(){
        onFiltersButtonStateChangeListener.setValue(1);
    }

    public void onTodayClicked(){
        onFiltersButtonStateChangeListener.setValue(2);
    }

    public void onFromFirstClicked(){
        onFiltersButtonStateChangeListener.setValue(3);
    }

    public MutableLiveData<Integer> getOnFiltersButtonStateChangeListener() {
        return onFiltersButtonStateChangeListener;
    }
}

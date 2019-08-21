package net.iGap.viewmodel;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class FragmentSeePayViewModel extends ViewModel {

    public MutableLiveData<Boolean> onAddClickListener = new MutableLiveData<>();
    public MutableLiveData<Boolean> onInquiryClickListener = new MutableLiveData<>();

    public FragmentSeePayViewModel() {
    }

    public void onAddCarClick(){
        onAddClickListener.postValue(true);
    }

    public void onInquiryClick(){
        onInquiryClickListener.postValue(true);
    }


}

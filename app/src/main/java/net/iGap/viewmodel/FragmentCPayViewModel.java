package net.iGap.viewmodel;


import androidx.lifecycle.MutableLiveData;

import net.iGap.repository.CPayRepository;
import net.iGap.model.cPay.UserPlaquesModel;

public class FragmentCPayViewModel extends BaseCPayViewModel<UserPlaquesModel> {

    private MutableLiveData<Boolean> onAddClickListener = new MutableLiveData<>();
    private MutableLiveData<Boolean> onInquiryClickListener = new MutableLiveData<>();
    private MutableLiveData<Boolean> onChargeClickListener = new MutableLiveData<>();
    private MutableLiveData<UserPlaquesModel> plaquesReceiverListener = new MutableLiveData<>();

    public FragmentCPayViewModel() {
        getPlaqueListByApi();
    }

    public void getPlaqueListByApi() {
        getLoaderListener().setValue(true);
        CPayRepository.getInstance().getAllUserPlaques(this, this);
    }

    public MutableLiveData<Boolean> getPlaqueChangeListener() {
        return CPayRepository.getInstance().getPlaquesChangeListener();
    }

    public MutableLiveData<Boolean> getOnAddClickListener() {
        return onAddClickListener;
    }

    public MutableLiveData<Boolean> getOnInquiryClickListener() {
        return onInquiryClickListener;
    }

    public MutableLiveData<UserPlaquesModel> getPlaquesReceiverListener() {
        return plaquesReceiverListener;
    }

    public MutableLiveData<Boolean> getOnChargeClickListener() {
        return onChargeClickListener;
    }

    public void onAddCarClick() {
        onAddClickListener.postValue(true);
    }

    public void onInquiryClick() {
        onInquiryClickListener.postValue(true);
    }

    public void onChargeClicked() {
        onChargeClickListener.setValue(true);
    }

    public void onRetryClicked() {
        getPlaqueListByApi();
    }

    @Override
    public void onSuccess(UserPlaquesModel data) {
        if (data == null || data.getData().size() == 0) {
            plaquesReceiverListener.setValue(null);
        } else {
            plaquesReceiverListener.setValue(data);
        }
    }
}

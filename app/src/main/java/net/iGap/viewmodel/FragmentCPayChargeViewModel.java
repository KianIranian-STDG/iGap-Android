package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;

import net.iGap.G;
import net.iGap.model.cPay.CPayUserWalletModel;

public class FragmentCPayChargeViewModel extends BaseCPayViewModel<CPayUserWalletModel> {

    private MutableLiveData<Boolean> editTextVisibilityListener = new MutableLiveData<>();
    public ObservableField<Boolean> isDark = new ObservableField<>(false);
    public ObservableField<Long> userCurrentAmount = new ObservableField<>(0L);
    private int mChargeAmount = 0;

    public FragmentCPayChargeViewModel() {
        isDark.set(G.isDarkTheme);
    }

    @Override
    public void onSuccess(CPayUserWalletModel data) {

    }

    public void onPaymentClicked(){

    }

    public void onSpinnerItemSelected(int position){

        editTextVisibilityListener.setValue(position == 6);

        switch (position){

            case 0 :
            case 6 :
                mChargeAmount = 0 ;
                break;

            case 1:
                mChargeAmount = 50000 ;
                break;

            case 2 :
                mChargeAmount = 100000;
                break;

            case 3 :
                mChargeAmount = 200000;
                break;

            case 4 :
                mChargeAmount = 500000 ;
                break;

            case 5:
                mChargeAmount = 1000000 ;
                break;
        }
    }

    public void onEditTextChangeListener(String amount){
        mChargeAmount = Integer.valueOf(amount);
    }

    public MutableLiveData<Boolean> getEditTextVisibilityListener() {
        return editTextVisibilityListener;
    }
}

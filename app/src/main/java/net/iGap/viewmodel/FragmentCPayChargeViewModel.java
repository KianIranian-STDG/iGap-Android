package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;

import net.iGap.G;
import net.iGap.api.repository.CPayRepository;
import net.iGap.model.cPay.CPayWalletAmountModel;
import net.iGap.model.cPay.PlaqueBodyModel;

public class FragmentCPayChargeViewModel extends BaseCPayViewModel<CPayWalletAmountModel> {

    private MutableLiveData<Boolean> editTextVisibilityListener = new MutableLiveData<>();
    public ObservableField<Boolean> isDark = new ObservableField<>(false);
    public ObservableField<String> userCurrentAmount = new ObservableField<>("-");
    private int mChargeAmount = 0;

    public FragmentCPayChargeViewModel() {
        isDark.set(G.isDarkTheme);
    }

    public void getRequestAmountFromServer(String plaque){
        getLoaderListener().setValue(true);
        CPayRepository.getInstance().getWalletAmount(new PlaqueBodyModel(plaque) , this);
    }

    @Override
    public void onSuccess(CPayWalletAmountModel data) {
        userCurrentAmount.set(data.getAmount());
        getLoaderListener().setValue(false);
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

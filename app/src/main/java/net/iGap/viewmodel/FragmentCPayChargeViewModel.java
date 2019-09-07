package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.api.errorhandler.ResponseCallback;
import net.iGap.api.repository.CPayRepository;
import net.iGap.model.cPay.CPayWalletAmountModel;
import net.iGap.model.cPay.ChargeWalletBodyModel;
import net.iGap.model.cPay.ChargeWalletModel;
import net.iGap.model.cPay.PlaqueBodyModel;

public class FragmentCPayChargeViewModel extends BaseCPayViewModel<CPayWalletAmountModel> {

    private MutableLiveData<Boolean> editTextVisibilityListener = new MutableLiveData<>();
    public ObservableField<Boolean> isDark = new ObservableField<>(false);
    public ObservableField<String> userCurrentAmount = new ObservableField<>("-");
    public ObservableInt secondaryLoaderVisiblity = new ObservableInt(View.GONE);
    public ObservableBoolean payButtonEnableState = new ObservableBoolean(true);
    private MutableLiveData<String> chargePaymentStateListener = new MutableLiveData<>();

    private long mChargeAmount = 0;
    private String mPlaque ;

    public FragmentCPayChargeViewModel() {
        isDark.set(G.isDarkTheme);
    }

    public void getRequestAmountFromServer(){
        getLoaderListener().setValue(true);
        CPayRepository.getInstance().getWalletAmount(new PlaqueBodyModel(mPlaque) , this);
    }

    @Override
    public void onSuccess(CPayWalletAmountModel data) {
        userCurrentAmount.set(data.getAmount());
        getLoaderListener().setValue(false);
    }

    public void onPaymentClicked(){

        if (mChargeAmount < 20000){
            getMessageToUser().setValue(R.string.amount_not_valid);
            return;
        }

        secondaryLoaderVisiblity.set(View.VISIBLE);
        payButtonEnableState.set(false);

        CPayRepository.getInstance().getChargeWallet(new ChargeWalletBodyModel(mPlaque, mChargeAmount), new ResponseCallback<ChargeWalletModel>() {
            @Override
            public void onSuccess(ChargeWalletModel data) {
                secondaryLoaderVisiblity.set(View.GONE);
                payButtonEnableState.set(true);
                chargePaymentStateListener.setValue(data.getToken());
            }

            @Override
            public void onError(ErrorModel error) {
                secondaryLoaderVisiblity.set(View.GONE);
                payButtonEnableState.set(true);
                getMessageToUserText().setValue(error.getMessage());
            }

            @Override
            public void onFailed(boolean handShakeError) {
                secondaryLoaderVisiblity.set(View.GONE);
                payButtonEnableState.set(true);
                getMessageToUser().setValue(R.string.server_do_not_response);
            }
        });

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

    public MutableLiveData<String> getChargePaymentStateListener() {
        return chargePaymentStateListener;
    }

    public void setPlaque(String mPlaque) {
        this.mPlaque = mPlaque;
    }
}

package net.iGap.viewmodel.mobileBank;


import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import net.iGap.R;
import net.iGap.model.mobileBank.BankPayLoanModel;
import net.iGap.model.mobileBank.BaseMobileBankResponse;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.MobileBankRepository;

public class MobileBankPayLoanBsViewModel extends BaseMobileBankViewModel {

    private final String AUTO_GET_DEPOSIT = "AUTO_GET_DEPOSIT";
    private final String CUSTOM_DEPOSIT = "CUSTOM_DEPOSIT";

    private SingleLiveEvent<Boolean> showLoader = new SingleLiveEvent<>();
    private SingleLiveEvent<String> responseListener = new SingleLiveEvent<>();
    private ObservableBoolean isDefaultDeposit = new ObservableBoolean(false);
    private ObservableField<String> amountText = new ObservableField<>();
    private ObservableField<String> secondaryPass = new ObservableField<>();
    private ObservableField<String> customDeposit = new ObservableField<>();
    private String mPaymentMethod = CUSTOM_DEPOSIT;
    private String mLoanNumber ;
    private int mMaxAmount ;

    public void onInquiryClicked(){

        if(amountText.get() != null && !amountText.get().isEmpty() && secondaryPass.get() != null && !secondaryPass.get().isEmpty()){
            if (Integer.parseInt(amountText.get()) <= mMaxAmount) {

                showLoader.setValue(true);
                MobileBankRepository.getInstance().getPayLoan(
                        mLoanNumber , getComputeDeposit(), amountText.get() , secondaryPass.get() , mPaymentMethod , true , this ,
                        new ResponseCallback<BaseMobileBankResponse<BankPayLoanModel>>() {
                            @Override
                            public void onSuccess(BaseMobileBankResponse<BankPayLoanModel> data) {
                                showLoader.setValue(false);
                                responseListener.postValue(data.getMessage());
                            }

                            @Override
                            public void onError(String error) {
                                showLoader.setValue(false);
                                getShowRequestErrorMessage().postValue(error);
                            }

                            @Override
                            public void onFailed() {
                                showLoader.setValue(false);
                            }
                        }
                );

            }else {
                getShowRequestErrorMessage().postValue("-2");
            }
        }else {
            getShowRequestErrorMessage().postValue("-1");
        }

    }

    private String getComputeDeposit() {
        if(mPaymentMethod.equals(AUTO_GET_DEPOSIT)){
            return null;
        }else {
            return customDeposit.get();
        }
    }

    public void onCheckedListener(int checkId){
        if(checkId == R.id.chDefault){
            isDefaultDeposit.set(true);
            mPaymentMethod = AUTO_GET_DEPOSIT;
        }else {
            isDefaultDeposit.set(false);
            mPaymentMethod = CUSTOM_DEPOSIT;
        }
    }

    public void setLoanNumber(String mLoanNumber) {
        this.mLoanNumber = mLoanNumber;
    }

    public void setMaxAmount(int mMaxAmount) {
        this.mMaxAmount = mMaxAmount;
        amountText.set(mMaxAmount + "");

    }

    public ObservableBoolean getIsDefaultDeposit() {
        return isDefaultDeposit;
    }

    public ObservableField<String> getAmountText() {
        return amountText;
    }

    public ObservableField<String> getSecondaryPass() {
        return secondaryPass;
    }

    public ObservableField<String> getCustomDeposit() {
        return customDeposit;
    }

    public SingleLiveEvent<Boolean> getShowLoader() {
        return showLoader;
    }

    public SingleLiveEvent<String> getResponseListener() {
        return responseListener;
    }

    public void setIsDefaultDeposit(ObservableBoolean isDefaultDeposit) {
        this.isDefaultDeposit = isDefaultDeposit;
    }

    public void setAmountText(ObservableField<String> amountText) {
        this.amountText = amountText;
    }

    public void setCustomDeposit(ObservableField<String> customDeposit) {
        this.customDeposit = customDeposit;
    }
}

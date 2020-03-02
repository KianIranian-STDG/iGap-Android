package net.iGap.viewmodel.mobileBank;


import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import net.iGap.R;

public class MobileBankPayLoanBsViewModel extends BaseMobileBankViewModel {

    private ObservableBoolean isDefaultDeposit = new ObservableBoolean(true);
    private ObservableField<String> amountText = new ObservableField<>();
    private ObservableField<String> secondaryPass = new ObservableField<>();
    private ObservableField<String> customDeposit = new ObservableField<>();

    public void onInquiryClicked(){

    }

    public void onCheckedListener(int checkId){
        if(checkId == R.id.chDefault){
            isDefaultDeposit.set(true);
        }else {
            isDefaultDeposit.set(false);
        }
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
}

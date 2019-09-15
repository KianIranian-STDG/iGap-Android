package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;

public class FragmentPaymentViewModel extends ViewModel {

    public MutableLiveData<Integer> goToPaymentBillPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToPaymentCharge = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToButInternetPackage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToCardToCardPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToPaymentInquiryPage = new MutableLiveData<>();

    public void onClickCharge() {
        goToPaymentCharge.setValue(true);
    }

    public void onClickCardToCard() {
        goToCardToCardPage.setValue(true);
    }

    public void onClickBill() {
        goToPaymentBillPage.setValue(R.string.pay_bills);
    }

    public void onClickBillTraffic() {
        goToPaymentBillPage.setValue(R.string.pay_bills_crime);
    }

    public void onClickInquiryMci() {
        goToPaymentInquiryPage.setValue(true);
    }

    public void onClickInquiryTelecom() {
        goToPaymentInquiryPage.setValue(false);
    }

    public void onClickBuyInternetPackage() {
        goToButInternetPackage.setValue(true);
    }

}

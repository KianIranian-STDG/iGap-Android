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
import androidx.databinding.ObservableField;
import androidx.annotation.NonNull;
import android.text.Html;

import net.iGap.G;
import net.iGap.R;
import net.iGap.model.GoToWalletPage;
import net.iGap.request.RequestInfoPage;
import net.iGap.request.RequestWalletRegister;

import ir.radsense.raadcore.model.Auth;

public class FragmentWalletAgreementViewModel extends ViewModel {


    private ObservableField<String> callbackTxtAgreement = new ObservableField<>("");
    private MutableLiveData<Boolean> showDialogAcceptTerms = new MutableLiveData<>();
    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<GoToWalletPage> goToWalletPage = new MutableLiveData<>();
    private String phone;
    private boolean isScan;

    public FragmentWalletAgreementViewModel(String mPhone, boolean isScan) {
        phone = mPhone;
        this.isScan = isScan;
        G.onReceivePageInfoWalletAgreement = body -> callbackTxtAgreement.set(Html.fromHtml(body).toString());
        new RequestInfoPage().infoPage("WALLET_AGREEMENT");
    }

    public ObservableField<String> getCallbackTxtAgreement() {
        return callbackTxtAgreement;
    }

    public MutableLiveData<Boolean> getShowDialogAcceptTerms() {
        return showDialogAcceptTerms;
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public MutableLiveData<GoToWalletPage> getGoToWalletPage() {
        return goToWalletPage;
    }

    public void checkBoxAgreement(boolean checked) {
        showDialogAcceptTerms.setValue(checked);
    }

    public void acceptTerms() {
        if (G.userLogin) {
            new RequestWalletRegister().walletRegister();
            goToWalletPage();
        } else {
            showErrorMessage.setValue(R.string.there_is_no_connection_to_server);
        }
    }

    private void goToWalletPage() {

        //ToDo: why have delay ?!!!
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Auth.getCurrentAuth() != null) {
                    goToWalletPage.setValue(new GoToWalletPage(phone, isScan));
                }else{
                    goToWalletPage();
                }
            }
        }, 100);

    }

}
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

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentPaymentBill;
import net.iGap.fragments.FragmentPaymentCharge;
import net.iGap.fragments.FragmentPaymentInquiry;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import ir.pec.mpl.pecpayment.view.CardToCardInitiator;
import net.iGap.request.RequestMplGetCardToCardToken;

import static net.iGap.activities.ActivityMain.requestCodeCardToCard;


public class FragmentPaymentViewModel {

    public ObservableField<Drawable> observeBackGround = new ObservableField<>();

    public FragmentPaymentViewModel(Bundle arguments) {

        Drawable myIcon = G.context.getResources().getDrawable(R.drawable.oval_green_sticker);
        myIcon.setColorFilter(Color.parseColor(G.appBarColor), PorterDuff.Mode.SRC_IN);
        observeBackGround.set(myIcon);
    }

    public void onClickCharge(View v) {
        new HelperFragment(FragmentPaymentCharge.newInstance()).setReplace(false).load();
    }

    public static void CallCardToCard() {
        if (G.currentActivity == null || G.currentActivity.isFinishing()) {
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(G.currentActivity, "",
                G.context.getString(R.string.please_wait), true);
        boolean isSend = new RequestMplGetCardToCardToken().mplGetToken(new RequestMplGetCardToCardToken.OnMplCardToCardToken() {
            @Override
            public void onToken(String token) {
                Intent intent = new Intent(G.context, CardToCardInitiator.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Token", token);
                G.currentActivity.startActivityForResult(intent , requestCodeCardToCard);
                dialog.dismiss();
            }

            @Override
            public void onError(int major, int minor) {
                HelperError.showSnackMessage(G.context.getString(R.string.wallet_error_server), false);
                dialog.dismiss();
            }
        });

        if (!isSend) {
            HelperError.showSnackMessage(G.context.getString(R.string.wallet_error_server), false);
            dialog.dismiss();
        }
    }

    public void onClickCardToCard(View v) {
        CallCardToCard();
    }

    public void onClickBill(View v) {
        new HelperFragment(FragmentPaymentBill.newInstance(R.string.pay_bills)).setReplace(false).load();
    }

    public void onClickBillTraffic(View v) {
        new HelperFragment(FragmentPaymentBill.newInstance(R.string.pay_bills_crime)).setReplace(false).load();
    }

    public void onClickInquiryMci(View v) {
        new HelperFragment(FragmentPaymentInquiry.newInstance(FragmentPaymentInquiryViewModel.OperatorType.mci, null)).setReplace(false).load();
    }

    public void onClickInquiryTelecom(View v) {
        new HelperFragment(FragmentPaymentInquiry.newInstance(FragmentPaymentInquiryViewModel.OperatorType.telecome, null)).setReplace(false).load();
    }

}

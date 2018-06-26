package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.os.Bundle;
import android.view.View;

import net.iGap.fragments.FragmentPayment;
import net.iGap.fragments.FragmentPaymentBill;
import net.iGap.fragments.FragmentPaymentCharge;
import net.iGap.helper.HelperFragment;


public class FragmentPaymentViewModel {

    public FragmentPaymentViewModel(Bundle arguments) {

    }

    public void onClickRippleBack(View v) {
        if (FragmentPayment.onBackFragment != null) {
            FragmentPayment.onBackFragment.onBack();
        }
    }


    public void onClickCharge(View v) {
        new HelperFragment(FragmentPaymentCharge.newInstance()).setReplace(false).load();
    }

    public void onClickBill(View v) {


        new HelperFragment(FragmentPaymentBill.newInstance()).setReplace(false).load();

    }

}

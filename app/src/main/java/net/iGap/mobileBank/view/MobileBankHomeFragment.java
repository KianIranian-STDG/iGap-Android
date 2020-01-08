package net.iGap.mobileBank.view;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;

public class MobileBankHomeFragment extends Fragment {

    public MobileBankHomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mobile_bank_home, container, false);
    }

}

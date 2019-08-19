package net.iGap.fragments.seePay;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;

public class FragmentSeePayHistory extends Fragment {


    public FragmentSeePayHistory() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_see_pay_history, container, false);
    }

}

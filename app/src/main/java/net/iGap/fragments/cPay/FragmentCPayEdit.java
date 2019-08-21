package net.iGap.fragments.cPay;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;

public class FragmentCPayEdit extends Fragment {

    public FragmentCPayEdit() {

    }

    public static FragmentCPayEdit getInstance(String plaque) {
        FragmentCPayEdit fragmentCPay = new FragmentCPayEdit();
        Bundle bundle = new Bundle();
        bundle.putString("plaque", plaque);
        fragmentCPay.setArguments(bundle);
        return fragmentCPay;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cpay_edit, container, false);
    }

}

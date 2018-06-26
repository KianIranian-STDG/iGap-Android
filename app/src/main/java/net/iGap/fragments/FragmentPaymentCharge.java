package net.iGap.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentPaymentChargeBinding;
import net.iGap.viewmodel.FragmentPaymentChargeViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPaymentCharge extends BaseFragment {

    public static FragmentPaymentCharge.OnBackFragment onBackFragment;
    private FragmentPaymentChargeViewModel fragmentPaymentChargeViewModel;
    private FragmentPaymentChargeBinding fragmentPaymentChargeBinding;

    public static FragmentPaymentCharge newInstance() {
        return new FragmentPaymentCharge();
    }

    public FragmentPaymentCharge() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentPaymentChargeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_charge, container, false);
        return attachToSwipeBack(fragmentPaymentChargeBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding(getArguments());

        onBackFragment = new FragmentPaymentCharge.OnBackFragment() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };
    }

    private void initDataBinding(Bundle arguments) {

        fragmentPaymentChargeViewModel = new FragmentPaymentChargeViewModel(fragmentPaymentChargeBinding);
        fragmentPaymentChargeBinding.setFragmentPaymentChargeViewModel(fragmentPaymentChargeViewModel);

    }


    public interface OnBackFragment {
        void onBack();
    }
}
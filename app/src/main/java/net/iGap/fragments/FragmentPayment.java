package net.iGap.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentPaymentBinding;
import net.iGap.viewmodel.FragmentPaymentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPayment extends BaseFragment {

    public static net.iGap.fragments.FragmentPayment.OnBackFragment onBackFragment;
    private FragmentPaymentViewModel fragmentPaymentViewModel;
    private FragmentPaymentBinding fragmentPaymentBinding;


    public static FragmentPayment newInstance() {
        return new FragmentPayment();
    }

    public FragmentPayment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentPaymentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false);
        return attachToSwipeBack(fragmentPaymentBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding(getArguments());

        onBackFragment = new net.iGap.fragments.FragmentPayment.OnBackFragment() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };
    }

    private void initDataBinding(Bundle arguments) {

        fragmentPaymentViewModel = new FragmentPaymentViewModel(arguments);
        fragmentPaymentBinding.setFragmentPaymentViewModel(fragmentPaymentViewModel);

    }


    public interface OnBackFragment {
        void onBack();
    }
}
package net.iGap.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentPaymentChargeBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.IBackHandler;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentPaymentChargeViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPaymentCharge extends BaseFragment {

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
    }

    private void initDataBinding(Bundle arguments) {

        fragmentPaymentChargeViewModel = new FragmentPaymentChargeViewModel(fragmentPaymentChargeBinding);
        fragmentPaymentChargeBinding.setFragmentPaymentChargeViewModel(fragmentPaymentChargeViewModel);

        IBackHandler iBackHandler = new IBackHandler() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.buy_charge))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });
        fragmentPaymentChargeBinding.fpcToolbar.addView(toolbar.getView());

        fragmentPaymentChargeBinding.setBackHandler(iBackHandler);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        G.onMplResult = null;
    }
}
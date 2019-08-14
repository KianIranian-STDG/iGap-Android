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
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.interfaces.IBackHandler;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.internetpackage.BuyInternetPackageFragment;
import net.iGap.viewmodel.FragmentPaymentViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPayment extends BaseFragment {

    private FragmentPaymentViewModel fragmentPaymentViewModel;
    private FragmentPaymentBinding fragmentPaymentBinding;


    public static FragmentPayment newInstance() {
        return new FragmentPayment();
    }

    public FragmentPayment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentPaymentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false);
        return attachToSwipeBack(fragmentPaymentBinding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HelperTracker.sendTracker(HelperTracker.TRACKER_FINANCIAL_SERVICES);
        initDataBinding();
    }

    private void initDataBinding() {

        fragmentPaymentViewModel = new FragmentPaymentViewModel();
        fragmentPaymentBinding.setFragmentPaymentViewModel(fragmentPaymentViewModel);

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
                .setDefaultTitle(getString(R.string.financial_services))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        fragmentPaymentBinding.fpToolbar.addView(toolbar.getView());

        fragmentPaymentBinding.setBackHandler(iBackHandler);

        fragmentPaymentViewModel.goToPaymentBillPage.observe(this, type -> {
            if (getActivity() != null && type != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentPaymentBill.newInstance(type)).setReplace(false).load();
            }
        });

        fragmentPaymentViewModel.goToPaymentInquiryPage.observe(this, type -> {
            if (getActivity() != null && type != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentPaymentInquiry.newInstance(type, null)).setReplace(false).load();
            }
        });

        fragmentPaymentViewModel.goToPaymentCharge.observe(this, go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentPaymentCharge.newInstance()).setReplace(false).load();
            }
        });

        fragmentPaymentViewModel.goToButInternetPackage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new BuyInternetPackageFragment()).setReplace(false).load(true);
            }
        });

    }
}
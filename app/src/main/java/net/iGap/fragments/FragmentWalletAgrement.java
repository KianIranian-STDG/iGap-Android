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
import net.iGap.databinding.FragmentWalletAgrementBinding;
import net.iGap.interfaces.IBackHandler;
import net.iGap.interfaces.OnReceivePageInfoWalletAgreement;
import net.iGap.request.RequestInfoPage;
import net.iGap.viewmodel.FragmentWalletAgreementViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWalletAgrement extends BaseFragment {

    private FragmentWalletAgrementBinding fragmentWalletAgrementBinding;

    public static FragmentWalletAgrement newInstance() {
        return new FragmentWalletAgrement();
    }

    public FragmentWalletAgrement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentWalletAgrementBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet_agrement, container, false);
        return attachToSwipeBack(fragmentWalletAgrementBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding(getArguments());
    }

    private void initDataBinding(Bundle arguments) {
        IBackHandler iBackHandler = new IBackHandler() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };

        fragmentWalletAgrementBinding.setBackHandler(iBackHandler);

        FragmentWalletAgreementViewModel fragmentWalletAgreementViewModel = new FragmentWalletAgreementViewModel(fragmentWalletAgrementBinding);
        fragmentWalletAgrementBinding.setFragmentWalletAgreementViewModel(fragmentWalletAgreementViewModel);


        G.onReceivePageInfoWalletAgreement = new OnReceivePageInfoWalletAgreement() {
            @Override
            public void onReceivePageInfo(String body) {
                fragmentWalletAgrementBinding.getFragmentWalletAgreementViewModel().callbackTxtAgreement.set(body);
            }
        };

        new RequestInfoPage().infoPage("WALLET_AGREEMENT");

    }
}
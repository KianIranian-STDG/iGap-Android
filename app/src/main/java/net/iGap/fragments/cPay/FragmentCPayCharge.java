package net.iGap.fragments.cPay;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentCpayChargeBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentCPayChargeViewModel;


public class FragmentCPayCharge extends BaseFragment implements ToolbarListener {

    private FragmentCPayChargeViewModel viewModel ;
    private FragmentCpayChargeBinding binding ;

    public FragmentCPayCharge() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentCPayChargeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_cpay_charge, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
    }

    private void initToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.charge))
                .setLeftIcon(R.string.back_icon)
                .setListener(this);

        binding.toolbar.addView(toolbar.getView());

    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }
}

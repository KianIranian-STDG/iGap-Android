package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.databinding.FragmentFinancialServicesBinding;
import net.iGap.viewmodel.FinancialServicesViewModel;

import org.jetbrains.annotations.NotNull;

public class FragmentFinancialServices extends FragmentToolBarBack {

    private FragmentFinancialServicesBinding binding;
    private FinancialServicesViewModel viewModel;

    public static FragmentFinancialServices newInstance() {
        FragmentFinancialServices fragmentFinancialServices = new FragmentFinancialServices();
        return fragmentFinancialServices;
    }

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_financial_services, root, true);
        viewModel = new FinancialServicesViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.walletPriceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once
                binding.walletPriceValue.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.root);
                set.constrainCircle(binding.walletPointer.getId(), binding.walletPriceView.getId(), binding.walletPriceView.getWidth() / 2, 0);
                set.applyTo(binding.root);
            }
        });

        viewModel.walletPointerPosition.observe(this, integer -> {
            if (integer != null) {
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.root);
                set.constrainCircle(binding.walletPointer.getId(), binding.walletPriceView.getId(), binding.walletPriceView.getWidth() / 2, integer);
                set.applyTo(binding.root);
            }
        });


    }

}

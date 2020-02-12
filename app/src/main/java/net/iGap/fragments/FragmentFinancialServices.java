package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;

import net.iGap.R;
import net.iGap.databinding.FragmentFinancialServicesBinding;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FinancialServicesViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

        HelperToolbar t = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.more_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.financial_services))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        showMenu();
                    }
                });

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

    private void showMenu() {
        if (getContext() != null) {
            List<String> items = new ArrayList<>();
            items.add(getString(R.string.financial_report));
            items.add(getString(R.string.settings));
            items.add(getString(R.string.fag));
            new TopSheetDialog(getContext()).setListData(items, -1, position -> {
                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }).show();
        }
    }

}

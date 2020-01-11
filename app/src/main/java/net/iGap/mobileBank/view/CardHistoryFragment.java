package net.iGap.mobileBank.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.MobileBankHistoryBinding;
import net.iGap.electricity_bill.view.ElectricityBillMainFrag;
import net.iGap.electricity_bill.view.ElectricityBillPayFrag;
import net.iGap.electricity_bill.viewmodel.ElectricityBillMainVM;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.mobileBank.viewmoedel.CardHistoryViewModel;

public class CardHistoryFragment extends BaseAPIViewFrag<CardHistoryViewModel> {

    private MobileBankHistoryBinding binding;
    private static final String TAG = "CardHistoryFragment";

    public static CardHistoryFragment newInstance() {
        return new CardHistoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CardHistoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_history, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        initial();
    }

    private void initial() {
        viewModel.init();
    }

}

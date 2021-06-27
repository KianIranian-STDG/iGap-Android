package net.iGap.fragments.mobileBank;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.MobileBankBottomSheetFragmentBinding;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.helper.HelperError;
import net.iGap.adapter.mobileBank.ShebaNumbersAdapter;
import net.iGap.viewmodel.mobileBank.MobileBankBottomSheetViewModel;

public class MobileBankBottomSheetFragment extends BaseBottomSheet {

    private static String NUMBER = "NUMBER";
    private static String IS_CARD = "IS_CARD";
    private MobileBankBottomSheetFragmentBinding binding;
    private MobileBankBottomSheetViewModel mViewModel;
    private String mNumber;
    private boolean isCard;

    public static MobileBankBottomSheetFragment newInstance(String number, boolean isCard) {
        MobileBankBottomSheetFragment fragment = new MobileBankBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NUMBER, number);
        bundle.putBoolean(IS_CARD, isCard);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_bottom_sheet_fragment, container, false);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.progress.getIndeterminateDrawable().setColorFilter(0XFFB6774E, android.graphics.PorterDuff.Mode.MULTIPLY);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MobileBankBottomSheetViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) dismiss();
        mNumber = getArguments().getString(NUMBER);
        isCard = getArguments().getBoolean(IS_CARD);
        setupListeners();
        getSheba();
    }

    private void getSheba() {
        if (isCard) {
            mViewModel.getShebaNumber(mNumber);
        } else {
            mViewModel.getShebaNumberByDeposit(mNumber);
        }
    }

    private void setupListeners() {

        mViewModel.getShebaListener().observe(getViewLifecycleOwner(), shebas -> {
            if (shebas == null || getActivity() == null) return;
            binding.rvItems.setNestedScrollingEnabled(false);
            binding.rvItems.setLayoutManager(new LinearLayoutManager(binding.rvItems.getContext()));
            binding.rvItems.setAdapter(new ShebaNumbersAdapter(shebas));
        });

        mViewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                dismiss();
                HelperError.showSnackMessage(msg, false);}
        });

        binding.btnClose.setOnClickListener(v -> dismiss());

        binding.btnRetry.setOnClickListener(v -> getSheba());
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

}

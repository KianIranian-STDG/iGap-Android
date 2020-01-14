package net.iGap.mobileBank.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.MobileBankTransferCtcStepOneBinding;
import net.iGap.mobileBank.viewmoedel.MobileBankTransferCTCStepOneViewModel;

public class MobileBankTransferCTCStepOneFragment extends BaseAPIViewFrag<MobileBankTransferCTCStepOneViewModel> {

    MobileBankTransferCtcStepOneBinding binding;

    private static final String TAG = "MobileBankTransferCTCSt";

    public static MobileBankTransferCTCStepOneFragment newInstance() {
        return new MobileBankTransferCTCStepOneFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankTransferCTCStepOneViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_transfer_ctc_step_one, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }
}

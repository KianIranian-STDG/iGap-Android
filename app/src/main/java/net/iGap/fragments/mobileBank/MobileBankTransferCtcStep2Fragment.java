package net.iGap.fragments.mobileBank;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.MobileBankTransferCtcStep2FragmentBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.mobileBank.MobileBankTransferCtcStep2ViewModel;

public class MobileBankTransferCtcStep2Fragment extends BaseMobileBankFragment<MobileBankTransferCtcStep2ViewModel> {

    private MobileBankTransferCtcStep2FragmentBinding binding;

    public static MobileBankTransferCtcStep2Fragment newInstance() {
        return new MobileBankTransferCtcStep2Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_transfer_ctc_step2_fragment, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankTransferCtcStep2ViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupListeners();
    }

    private void setupToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setRoundBackground(false)
                .setLeftIcon(R.string.icon_back)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        binding.toolbar.addView(toolbar.getView());

    }

    private void setupListeners() {

        binding.btnConfirm.setOnClickListener(v -> {
            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new MobileBankTransferCtcStep3Fragment())
                        .setReplace(false)
                        .load();
            }
        });

    }
}

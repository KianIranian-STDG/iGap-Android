package net.iGap.igasht.barcodescaner;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtBarcodeScanerBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.igasht.IGashtBaseView;
import net.iGap.interfaces.ToolbarListener;

public class FragmentIgashtBarcodeScan extends IGashtBaseView {

    private FragmentIgashtBarcodeScanerBinding binding;
    private IGashtBarcodeScannerViewModel viewModel;

    public static FragmentIgashtBarcodeScan getInstance(String voucherNumber) {
        FragmentIgashtBarcodeScan fragmentIgashtBarcodeScan = new FragmentIgashtBarcodeScan();
        Bundle bundle = new Bundle();
        bundle.putString("voucher_number", voucherNumber);
        fragmentIgashtBarcodeScan.setArguments(bundle);
        return fragmentIgashtBarcodeScan;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new IGashtBarcodeScannerViewModel(getArguments() != null ? getArguments().getString("voucher_number") : "");
            }
        }).get(IGashtBarcodeScannerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_barcode_scaner, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).getView());

        viewModel.getShowQRCodeImage().observe(getViewLifecycleOwner(), imageBitmap -> {
            if (imageBitmap != null) {
                binding.barCodeImage.setImageBitmap(imageBitmap);
            }
        });
    }
}

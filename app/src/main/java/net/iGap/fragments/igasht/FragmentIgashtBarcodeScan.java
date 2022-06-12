package net.iGap.fragments.igasht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtBarcodeScanerBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.messenger.theme.Theme;
import net.iGap.viewmodel.igasht.IGashtBarcodeScannerViewModel;
import net.iGap.observers.interfaces.ToolbarListener;

public class FragmentIgashtBarcodeScan extends IGashtBaseView<IGashtBarcodeScannerViewModel> {

    private FragmentIgashtBarcodeScanerBinding binding;

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
        binding.paymentStatus.setTextColor(Theme.getColor(Theme.key_title_text));
        binding.paymentCode.setTextColor(Theme.getColor(Theme.key_title_text));
        binding.v.setTextColor(Theme.getColor(Theme.key_subtitle_text));

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.icon_back)
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

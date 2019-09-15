package net.iGap.dialog.payment.buyinternetpackage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.R;
import net.iGap.databinding.FragmentBottomSheetBuyInternetPackageBinding;
import net.iGap.dialog.payment.CompleteListener;

public class BuyInternetPackageBottomSheetDialog extends BottomSheetDialogFragment {

    private BuyInternetPackageViewModel viewModel;
    private CompleteListener completeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetBuyInternetPackageBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_buy_internet_package, container, false);
        viewModel = new BuyInternetPackageViewModel(completeListener);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    public void setCompleteListener(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }
}

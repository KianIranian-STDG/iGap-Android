package net.iGap.dialog.payment.charity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentBottomSheetCharityConfirmBinding;
import net.iGap.dialog.payment.CompleteListener;

public class CharityConfirmBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String CHARITY_NAME="charityName";
    private static final String CHARITY_AMOUNT = "charityAmount";
    private CharityConfirmViewModel viewModel;
    private CompleteListener completeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetCharityConfirmBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_charity_confirm, container, false);
        String charityName = "",charityAmount ="";
        if (getArguments()!=null){
            charityName= getArguments().getString(CHARITY_NAME);
            charityAmount = getArguments().getString(CHARITY_AMOUNT);
        }
        viewModel = new CharityConfirmViewModel(charityName,charityAmount,completeListener);
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

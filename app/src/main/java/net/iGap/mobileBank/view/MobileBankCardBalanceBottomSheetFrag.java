package net.iGap.mobileBank.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.MobileBankCardBalanceBottomSheetDialogBinding;
import net.iGap.dialog.BaseBottomSheet;
import net.iGap.mobileBank.viewmoedel.MobileBankCardBalanceViewModel;

public class MobileBankCardBalanceBottomSheetFrag extends BaseBottomSheet {

    private MobileBankCardBalanceBottomSheetDialogBinding binding;
    private MobileBankCardBalanceViewModel viewModel;
    private CompleteListener completeListener;

    public static MobileBankCardBalanceBottomSheetFrag newInstance(String cardNumber) {
        MobileBankCardBalanceBottomSheetFrag frag = new MobileBankCardBalanceBottomSheetFrag();
        Bundle bundle = new Bundle();
        bundle.putString("cardNumber", cardNumber);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(MobileBankCardBalanceViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_card_balance_bottom_sheet_dialog, container, false);
        binding.setBottomSheetViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.setCardNumber(getArguments().getString("cardNumber"));
        onComplete();
        onTextChange();
    }

    private void onComplete() {
        viewModel.getComplete().observe(getViewLifecycleOwner(), aBoolean -> {

            completeListener.onCompleted(aBoolean);
            this.dismiss();

        });
    }

    private void onTextChange() {
        binding.cardNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.cardNumber.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.password.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.CVVET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.CVV.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.expireDateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.expireDate.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    interface CompleteListener {
        void onCompleted(boolean result);
    }

    public void setCompleteListener(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }
}

package net.iGap.fragments.electricity_bill;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.databinding.FragmentElecBillAddBinding;
import net.iGap.helper.HelperError;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.bill.BillInfo;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.viewmodel.electricity_bill.ElectricityBillAddVM;

public class ElectricityBillAddFrag extends BaseBottomSheet {

    private FragmentElecBillAddBinding binding;
    private ElectricityBillAddVM viewModel;
    private BillInfo info;
    private boolean editMode = false;
    private CompleteListener completeListener;
    private static final String TAG = "ElectricityBillAddFrag";

    public static ElectricityBillAddFrag newInstance() {
        return new ElectricityBillAddFrag();
    }

    public static ElectricityBillAddFrag newInstance(BillInfo info, boolean editMode) {
        return new ElectricityBillAddFrag(info, editMode);
    }


    private ElectricityBillAddFrag(BillInfo billID, boolean editMode) {
        this.info = billID;
        this.editMode = editMode;
    }

    public ElectricityBillAddFrag() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ElectricityBillAddVM.class);
        viewModel.setInfo(info);
        viewModel.setEditMode(editMode);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_bill_add, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.lineViewTop.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bottom_sheet_dialog_line), getContext(), Theme.getColor(Theme.key_theme_color)));
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        binding.billName.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        binding.billId.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        binding.billDesc.setTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_title_text)));
        binding.billNameET.setTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_title_text)));
        binding.billidET.setTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_title_text)));
        switch (info.getBillType()) {
            case GAS:
                binding.billId.setHint(getResources().getString(R.string.elecBill_main_billIDHint3));
                viewModel.getBillID().set(info.getGasID());
                break;
            case PHONE:
                binding.billId.setHint(getResources().getString(R.string.elecBill_main_billIDHint2));
                viewModel.getBillID().set(info.getAreaCode() + info.getPhoneNum());
                break;
            case MOBILE:
                binding.billId.setHint(getResources().getString(R.string.mobile_number));
                viewModel.getBillID().set(info.getPhoneNum());
                break;
            default:
                viewModel.getBillID().set(info.getBillID());
                break;
        }

        viewModel.getErrorM().observe(getViewLifecycleOwner(), errorModel -> {
            if (errorModel != null) {
                showDialog(getResources().getString(R.string.elecBill_error_title), errorModel);
            }
        });

        viewModel.getSuccessM().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                showDialog(getResources().getString(R.string.elecBill_success_title), message);
                if (completeListener != null)
                    completeListener.loadAgain();
            }
        });

        viewModel.getErrorRequestFailed().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                HelperError.showSnackMessage(getString(message), false);
            }
        });

        viewModel.getGoBack().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                dismiss();
        });
        cancelErrorWhileTyping();

        if (editMode) {
            binding.submitBill.setText(getResources().getString(R.string.elecBill_edit_saveBtn));
            viewModel.getBillName().set(info.getTitle());
        }
    }

    private void cancelErrorWhileTyping() {
        binding.billNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getBillNameErrorEnable().set(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.billidET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getBillIDErrorEnable().set(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showDialog(String title, String message) {
        new MaterialDialog.Builder(getContext()).title(title).positiveText(getResources().getString(R.string.ok)).content(message).show();
    }

    interface CompleteListener {
        void loadAgain();
    }

    public void setCompleteListener(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }
}

package net.iGap.electricity_bill.view;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.databinding.FragmentElecBillAddBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.electricity_bill.viewmodel.ElectricityBillAddVM;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

public class ElectricityBillAddFrag extends BaseAPIViewFrag {

    private FragmentElecBillAddBinding binding;
    private ElectricityBillAddVM elecBillVM;
    private String billID, billTitle, nationalID;
    private boolean editMode = false;
    private static final String TAG = "ElectricityBillAddFrag";

    public static ElectricityBillAddFrag newInstance() {
        return new ElectricityBillAddFrag();
    }

    public static ElectricityBillAddFrag newInstance(String billID, boolean editMode) {
        return new ElectricityBillAddFrag(billID, editMode);
    }

    public static ElectricityBillAddFrag newInstance(String billID, String billTitle, String nationalID, boolean editMode) {
        return new ElectricityBillAddFrag(billID, billTitle, nationalID, editMode);
    }

    private ElectricityBillAddFrag(String billID, boolean editMode) {
        this.billID = billID;
        this.editMode = editMode;
    }

    public ElectricityBillAddFrag() {
    }

    private ElectricityBillAddFrag(String billID, String billTitle, String nationalID, boolean editMode) {
        this.billID = billID;
        this.billTitle = billTitle;
        this.nationalID = nationalID;
        this.editMode = editMode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elecBillVM = ViewModelProviders.of(this).get(ElectricityBillAddVM.class);
        elecBillVM.getBillID().set(billID);
        if (editMode) {
            elecBillVM.getBillName().set(billTitle);
            elecBillVM.getBillUserID().set(nationalID);
            elecBillVM.setEditMode(editMode);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_bill_add, container, false);
        binding.setViewmodel(elecBillVM);
        binding.setLifecycleOwner(this);
        this.viewModel = elecBillVM;

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
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

        elecBillVM.getErrorM().observe(getViewLifecycleOwner(), errorModel -> {
            if (errorModel.getName().equals("200"))
                showDialog(getResources().getString(R.string.elecBill_success_title), errorModel.getMessage());
            else
                showDialog(getResources().getString(R.string.elecBill_error_title), errorModel.getMessage());
        });

        elecBillVM.getGoBack().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                popBackStackFragment();
        });
        cancelErrorWhileTyping();

        if (editMode)
            binding.submitBill.setText(getResources().getString(R.string.elecBill_edit_saveBtn));
    }

    private void cancelErrorWhileTyping() {
        binding.billNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                elecBillVM.getBillNameErrorEnable().set(false);
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
                elecBillVM.getBillIDErrorEnable().set(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.billMobileET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                elecBillVM.getBillPhoneErrorEnable().set(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.billUserIDET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                elecBillVM.getBillUserIDErrorEnable().set(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.billEmailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                elecBillVM.getBillEmailErrorEnable().set(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showDialog(String title, String message) {
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        defaultRoundDialog.setTitle(title);
        defaultRoundDialog.setMessage(message);
        defaultRoundDialog.setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> dialog.dismiss());
        defaultRoundDialog.show();
    }

}

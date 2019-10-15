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
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBillAddBinding;
import net.iGap.electricity_bill.viewmodel.ElectricityBillAddVM;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

public class ElectricityBillAddFrag extends BaseAPIViewFrag {

    private FragmentElecBillAddBinding binding;
    private ElectricityBillAddVM elecBillVM;
    private String billID;
    private static final String TAG = "ElectricityBillAddFrag";

    public static ElectricityBillAddFrag newInstance() {
        ElectricityBillAddFrag Frag = new ElectricityBillAddFrag();
        return Frag;
    }

    public static ElectricityBillAddFrag newInstance(String billID) {
        ElectricityBillAddFrag Frag = new ElectricityBillAddFrag(billID);
        return Frag;
    }

    public ElectricityBillAddFrag(String billID) {
        this.billID = billID;
    }

    public ElectricityBillAddFrag() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elecBillVM = ViewModelProviders.of(this).get(ElectricityBillAddVM.class);
        elecBillVM.getBillID().set(billID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_bill_add, container, false);
        binding.setViewmodel(elecBillVM);
        binding.setLifecycleOwner(this);
        this.viewModel = elecBillVM;

        return binding.getRoot();

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

        cancelErrorWhileTyping();
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


}

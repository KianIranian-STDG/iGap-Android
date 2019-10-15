package net.iGap.electricity_bill.view;

import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBillMainBinding;
import net.iGap.databinding.FragmentElecBillPayBinding;
import net.iGap.electricity_bill.repository.model.Bill;
import net.iGap.electricity_bill.viewmodel.ElectricityBillAddVM;
import net.iGap.electricity_bill.viewmodel.ElectricityBillMainVM;
import net.iGap.electricity_bill.viewmodel.ElectricityBillPayVM;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.PermissionHelper;
import net.iGap.interfaces.ToolbarListener;

import static net.iGap.activities.ActivityMain.electricityBillRequestCodeQrCode;

public class ElectricityBillPayFrag extends BaseAPIViewFrag {

    public enum btnActions {PAY_BILL, BRANCH_INFO, ADD_LIST}

    private FragmentElecBillPayBinding binding;
    private ElectricityBillPayVM elecBillVM;
    private Bill bill;
    private static final String TAG = "ElectricityBillPayFrag";

    public static ElectricityBillPayFrag newInstance(String billID, String billPayID, String billPrice) {
        ElectricityBillPayFrag Frag = new ElectricityBillPayFrag(new Bill(billID, billPayID, billPrice, null));
        return Frag;
    }

    public static ElectricityBillPayFrag newInstance(String billID) {
        ElectricityBillPayFrag Frag = new ElectricityBillPayFrag(new Bill(billID, null, null, null));
        return Frag;
    }

    public ElectricityBillPayFrag(Bill bill) {
        this.bill = bill;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elecBillVM = ViewModelProviders.of(this).get(ElectricityBillPayVM.class);
        elecBillVM.getBillID().set(bill.getID());
        if (bill.getPayID() != null)
            elecBillVM.getBillPayID().set(bill.getPayID());
        if (bill.getPrice() != null) {
            elecBillVM.getBillPrice().set(bill.getPrice());
            elecBillVM.getProgressVisibilityData().set(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_bill_pay, container, false);
        binding.setViewmodel(elecBillVM);
        binding.setFragment(this);
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

    }

    public void onPayBtnClick() {
        onBtnClickManger(btnActions.PAY_BILL);
    }

    public void onBranchInfoBtnClick() {
        onBtnClickManger(btnActions.BRANCH_INFO);
    }

    public void onAddToListBtnClick() {
        onBtnClickManger(btnActions.ADD_LIST);
    }

    private void onBtnClickManger(btnActions actions) {
        switch (actions) {
            case PAY_BILL:
                break;
            case BRANCH_INFO:
                break;
            case ADD_LIST:
                new HelperFragment(getFragmentManager(), ElectricityBillAddFrag.newInstance(elecBillVM.getBillID().get())).setReplace(false).load();
                break;
        }
    }

}

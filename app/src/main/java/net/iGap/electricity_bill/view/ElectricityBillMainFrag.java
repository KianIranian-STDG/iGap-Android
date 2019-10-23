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
import androidx.lifecycle.ViewModelProviders;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBillMainBinding;
import net.iGap.electricity_bill.viewmodel.ElectricityBillMainVM;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.PermissionHelper;
import net.iGap.interfaces.ToolbarListener;

import static net.iGap.activities.ActivityMain.electricityBillRequestCodeQrCode;

public class ElectricityBillMainFrag extends BaseAPIViewFrag {

    public enum btnActions {MY_ELEC_BILLS, SEARCH_BILL, BRANCH_INFO, QR_SCAN}

    private FragmentElecBillMainBinding binding;
    private ElectricityBillMainVM elecBillVM;
    private static final String TAG = "ElectricityBillMainFrag";

    public static ElectricityBillMainFrag newInstance() {
        ElectricityBillMainFrag kuknosLoginFrag = new ElectricityBillMainFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elecBillVM = ViewModelProviders.of(this).get(ElectricityBillMainVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_bill_main, container, false);
        binding.setViewmodel(elecBillVM);
        binding.setFragment(this);
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

        binding.billIdET.addTextChangedListener(new TextWatcher() {
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

        elecBillVM.getGoToBillDetailFrag().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                elecBillVM.getProgressVisibility().set(View.GONE);
                new HelperFragment(getFragmentManager(), ElectricityBillPayFrag.newInstance(elecBillVM.getBillID().get(), elecBillVM.getBillPayID(), elecBillVM.getBillPrice(), false)).setReplace(false).load();
            }
        });
    }

    public void onMyElecListBtnClick() {
        onBtnClickManger(btnActions.MY_ELEC_BILLS);
    }

    public void onSearchBillsBtnClick() {
        onBtnClickManger(btnActions.SEARCH_BILL);
    }

    public void onBranchInfoBtnClick() {
        onBtnClickManger(btnActions.BRANCH_INFO);
    }

    public void onQRScanBtnClick() {
        onBtnClickManger(btnActions.QR_SCAN);
    }

    private void onBtnClickManger(btnActions actions) {
        switch (actions) {
            case MY_ELEC_BILLS:
                new HelperFragment(getFragmentManager(), ElectricityBillListFrag.newInstance()).setReplace(false).load();
                break;
            case SEARCH_BILL:
                break;
            case BRANCH_INFO:
                break;
            case QR_SCAN:
                PermissionHelper permissionHelper = new PermissionHelper(getActivity(), this);
                if (permissionHelper.grantCameraPermission()) {
                    goToScannerActivity();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.CameraPermissionRequestCode) {
            boolean tmp = true;
            for (int grantResult : grantResults) {
                tmp = tmp && grantResult == PackageManager.PERMISSION_GRANTED;
            }
            if (tmp) {
                goToScannerActivity();
            }
        }

    }

    private void goToScannerActivity() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_128);
        integrator.setRequestCode(electricityBillRequestCodeQrCode);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("");
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == electricityBillRequestCodeQrCode) {
            IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
            elecBillVM.setDataFromBarcodeScanner(result.getContents());
        }

    }

}

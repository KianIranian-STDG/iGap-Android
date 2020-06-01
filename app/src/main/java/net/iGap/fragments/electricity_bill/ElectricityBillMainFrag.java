package net.iGap.fragments.electricity_bill;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.PermissionHelper;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.electricity_bill.ElectricityBillMainVM;

import static net.iGap.activities.ActivityMain.electricityBillRequestCodeQrCode;

public class ElectricityBillMainFrag extends BaseAPIViewFrag<ElectricityBillMainVM> {

    public enum btnActions {MY_ELEC_BILLS, SEARCH_BILL, BRANCH_INFO, QR_SCAN}

    private FragmentElecBillMainBinding binding;
    private static final String TAG = "ElectricityBillMainFrag";

    public static ElectricityBillMainFrag newInstance() {
        return new ElectricityBillMainFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ElectricityBillMainVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_bill_main, container, false);
        binding.setViewmodel(viewModel);
        binding.setFragment(this);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.billsOptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.billSpinner.setAdapter(adapter);
        binding.billSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        viewModel.setType(ElectricityBillMainVM.BillType.ELECTRICITY);
                        binding.billIdHolder.setHint(getResources().getString(R.string.elecBill_main_billIDHint));
                        binding.billIdHolder.setCounterMaxLength(13);
                        break;
                    case 1:
                        viewModel.setType(ElectricityBillMainVM.BillType.GAS);
                        binding.billIdHolder.setHint(getResources().getString(R.string.elecBill_main_billIDHint3));
                        binding.billIdHolder.setCounterMaxLength(12);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.modeGroup.setOnCheckedChangeListener((group, checkedId) -> onModeChangeView(checkedId));

        binding.billIdET.addTextChangedListener(new TextWatcher() {
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

        viewModel.getGoToBillDetailFrag().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                viewModel.getProgressVisibility().set(View.GONE);
                new HelperFragment(getFragmentManager(), ElectricityBillPayFrag.newInstance(
                        viewModel.getBillID().get(), viewModel.getBillPayID(), viewModel.getBillPrice(), false))
                        .setReplace(false).load();
            }
        });
    }

    private void onModeChangeView(int view) {
        switch (view) {
            case R.id.mode_serviceBill:
                binding.billSpinner.setVisibility(View.VISIBLE);
                binding.billQRscan.setVisibility(View.VISIBLE);
                binding.billTypeTitle.setText(getResources().getString(R.string.elecBill_main_billTypeTitle));
                binding.billIdHolder.setHint(getResources().getString(R.string.elecBill_main_billIDHint));
                binding.billIdHolder.setCounterMaxLength(13);
                viewModel.setType(ElectricityBillMainVM.BillType.ELECTRICITY);
                break;
            case R.id.mode_phoneBill:
                binding.billSpinner.setVisibility(View.GONE);
                binding.billQRscan.setVisibility(View.GONE);
                binding.billTypeTitle.setText(getResources().getString(R.string.elecBill_main_billPhoneTitle));
                binding.billIdHolder.setHint(getResources().getString(R.string.elecBill_main_billIDHint2));
                binding.billIdHolder.setCounterMaxLength(11);
                viewModel.setType(ElectricityBillMainVM.BillType.PHONE);
                break;
        }
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
                new HelperFragment(getFragmentManager(), ElectricityBillListFrag.newInstance()).setTag(ElectricityBillListFrag.class.getName()).setReplace(false).load();
                break;
            case SEARCH_BILL:
                new HelperFragment(getFragmentManager(), ElectricityBillSearchListFrag.newInstance()).setReplace(false).load();
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
            viewModel.setDataFromBarcodeScanner(result.getContents());
        }

    }

}

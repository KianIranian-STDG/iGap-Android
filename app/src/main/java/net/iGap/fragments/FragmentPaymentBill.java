package net.iGap.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.iGap.R;
import net.iGap.databinding.FragmentPaymentBillBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.PermissionHelper;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentPaymentBillViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import static net.iGap.activities.ActivityMain.requestCodeBarcode;


public class FragmentPaymentBill extends BaseFragment {

    private FragmentPaymentBillBinding billBinding;
    private FragmentPaymentBillViewModel viewModel;
    private boolean isPolice = false;

    public static FragmentPaymentBill newInstance(int resTitleId) {
        return FragmentPaymentBill.newInstance(resTitleId, null, null);
    }

    public static FragmentPaymentBill newInstance(int resTitleId, String PID, String BID) {
        Bundle args = new Bundle();
        args.putInt("title", resTitleId);
        args.putString("PID", PID);
        args.putString("BID", BID);
        FragmentPaymentBill fragmentPaymentBill = new FragmentPaymentBill();
        fragmentPaymentBill.setArguments(args);
        return fragmentPaymentBill;
    }

    public static FragmentPaymentBill newInstance(int resTitleId, JSONObject jsonObject) {
        String PID = null;
        String BID = null;
        try {
            PID = jsonObject.getString("PID");
            BID = jsonObject.getString("BID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newInstance(resTitleId, PID, BID);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

                String PID = null;
                String BID = null;
                if (getArguments() != null) {
                    isPolice = R.string.pay_bills_crime == getArguments().getInt("title");
                    PID = getArguments().getString("PID");
                    BID = getArguments().getString("BID");
                }
                return (T) new FragmentPaymentBillViewModel(isPolice, PID, BID);
            }
        }).get(FragmentPaymentBillViewModel.class);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        billBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_bill, container, false);
        billBinding.setViewModel(viewModel);
        billBinding.setLifecycleOwner(this);
        return attachToSwipeBack(billBinding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isPolice) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_FINE_BILL_PAGE);
        } else {
            HelperTracker.sendTracker(HelperTracker.TRACKER_SERVICE_BILL_PAGE);
        }

        billBinding.fpbLayoutToolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setDefaultTitle(getString(getArguments() != null ? getArguments().getInt("title") : R.string.pay_bills_crime))
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                }).getView());

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), errorMessageId -> {
            if (errorMessageId != null) {
                HelperError.showSnackMessage(getString(errorMessageId), false);
            }
        });

        viewModel.getGoToScannerPage().observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() != null && isGo != null && isGo) {
                PermissionHelper permissionHelper = new PermissionHelper(getActivity());
                if (permissionHelper.grantCameraPermission()) {
                    goToScannerActivity();
                }
            }
        });

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (isGoBack != null && isGoBack) {
                popBackStackFragment();
            }
        });

        viewModel.getHideKeyword().observe(getViewLifecycleOwner(), isHide -> {
            if (isHide != null && isHide) {
                hideKeyboard();
            }
        });

        viewModel.getBillTypeImage().observe(getViewLifecycleOwner(), integer -> billBinding.billTypeImage.setImageResource(integer));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCodeBarcode) {
            IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
            viewModel.setDataFromBarcodeScanner(result.getContents());
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
        integrator.setRequestCode(requestCodeBarcode);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("");
        integrator.initiateScan();
    }
}
package net.iGap.fragments.qrCodePayment.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.Result;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.databinding.FragmentScanCodeQRCodePaymentBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.qrCodePayment.viewModels.ScanCodeQRCodePaymentViewModel;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.PermissionHelper;
import net.iGap.libs.codescanner.AutoFocusMode;
import net.iGap.libs.codescanner.CodeScanner;
import net.iGap.libs.codescanner.DecodeCallback;
import net.iGap.libs.codescanner.ScanMode;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.model.qrCodePayment.MerchantInfo;
import net.iGap.observers.interfaces.OnGetPermission;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanCodeQRCodePaymentFragment extends BaseFragment {

    private FragmentScanCodeQRCodePaymentBinding mBinding;
    private ScanCodeQRCodePaymentViewModel mViewModel;
    private Toolbar mToolbar;
    private CodeScanner mCodeScanner;


    public static ScanCodeQRCodePaymentFragment newInstance() {
        ScanCodeQRCodePaymentFragment fragment = new ScanCodeQRCodePaymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ScanCodeQRCodePaymentViewModel(getActivity());
            }
        }).get(ScanCodeQRCodePaymentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_code_q_r_code_payment, container, false);
        mBinding.setLifecycleOwner(this);
        mBinding.setViewModel(mViewModel);
        checkCameraPermission();
        initObservers();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCodeScanner != null) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    public void onPause() {
        if (mCodeScanner != null) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
    }

    private void checkCameraPermission() {
        PackageManager packageManager = context.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(context, context.getString(R.string.device_dosenot_camera_en), Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
            return;
        }
        try {
            HelperPermission.getCameraPermission(context, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {
                    initCodeScanner();
                }

                @Override
                public void deny() {
                    getActivity().onBackPressed();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("ResourceType")
    private void initToolbar() {
        mToolbar = new Toolbar(getActivity());
        mToolbar.setTitle(G.isAppRtl ? R.string.logo_igap_fa : R.string.logo_igap_en);
        mToolbar.setBackIcon(new BackDrawable(false));
        mToolbar.setListener(new Toolbar.ToolbarListener() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    getActivity().onBackPressed();
                }
            }
        });
        mBinding.toolbarContainer.addView(mToolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 64, Gravity.TOP));
    }

    private void initCodeScanner() {
        mCodeScanner = new CodeScanner(getActivity(), mBinding.codeScanner);
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        mCodeScanner.setScanMode(ScanMode.SINGLE);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFlashEnabled(false);
        mCodeScanner.startPreview();

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                G.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.progressBar.setVisibility(View.VISIBLE);
                        String[] rawResult = result.getText().split("=");
                        String qrCode = rawResult[1];
                        Call<MerchantInfo> call = new RetrofitFactory().getPecQrRetrofit().getMerchantInfo(qrCode);
                        call.enqueue(new Callback<MerchantInfo>() {
                            @Override
                            public void onResponse(Call<MerchantInfo> call, Response<MerchantInfo> response) {
                                MerchantInfo merchantInfo = response.body();
                                assert merchantInfo != null;
                                new HelperFragment(
                                        getActivity().getSupportFragmentManager(),
                                        QRCodePaymentFragment.newInstance(merchantInfo.getMerchantName(), merchantInfo.getQrCode(), merchantInfo.isPcqr()))
                                        .setReplace(true)
                                        .setAddToBackStack(true)
                                        .load();
                                mBinding.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(Call<MerchantInfo> call, Throwable t) {
                                Log.e("onFailure", "onFailure: " + t.getMessage());
                            }
                        });
                    }
                });
            }
        });

        mBinding.codeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCodeScanner.startPreview();
            }
        });
    }

    private void initObservers() {

        mViewModel.getManuallyEnterButtonClick().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    new HelperFragment(getParentFragmentManager(), EnterCodeQRCodePaymentFragment.newInstance())
                            .setAddToBackStack(true)
                            .setReplace(false)
                            .load();
                }
            }
        });
    }
}
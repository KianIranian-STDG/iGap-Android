package net.iGap.fragments.qrCodePayment.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.databinding.FragmentEnterCodeQRCodePaymentBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.qrCodePayment.viewModels.EnterCodeQRPaymentViewModel;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.model.qrCodePayment.MerchantInfo;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterCodeQRCodePaymentFragment extends BaseFragment {

    private FragmentEnterCodeQRCodePaymentBinding mBinding;
    private EnterCodeQRPaymentViewModel mViewModel;
    private Toolbar mToolbar;

    public static EnterCodeQRCodePaymentFragment newInstance() {
        EnterCodeQRCodePaymentFragment fragment = new EnterCodeQRCodePaymentFragment();
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
                return (T) new EnterCodeQRPaymentViewModel();
            }
        }).get(EnterCodeQRPaymentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_enter_code_q_r_code_payment, container, false);
        mBinding.setLifecycleOwner(this);
        mBinding.setViewModel(mViewModel);
        setListeners();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
    }

    @SuppressLint("ResourceType")
    private void initToolbar() {
        mToolbar = new Toolbar(getActivity());
        mToolbar.setTitle(G.isAppRtl ? R.string.logo_igap_fa : R.string.logo_igap_en);
        mToolbar.setBackIcon(R.drawable.ic_back_btn);
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


    public void setListeners(){
        mViewModel.getCodeRegistrationClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                String inputCode = mBinding.codeEditText.getText().toString();
                if(inputCode == null || inputCode.length() == 0){
                    Toast.makeText(getActivity(), R.string.enter_code, Toast.LENGTH_SHORT).show();
                } else if(inputCode.length() != 0 && inputCode.length() != 10){
                    Toast.makeText(getActivity(), R.string.code_length_is_10_digit, Toast.LENGTH_SHORT).show();
                } else {
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    Call<MerchantInfo> call = new RetrofitFactory().getPecQrRetrofit().getMerchantInfo(inputCode);
                    call.enqueue(new Callback<MerchantInfo>() {
                        @Override
                        public void onResponse(Call<MerchantInfo> call, Response<MerchantInfo> response) {
                            if(response.isSuccessful()) {
                                MerchantInfo merchantInfo = response.body();
                                assert merchantInfo != null;
                                new HelperFragment(
                                        getActivity().getSupportFragmentManager(),
                                        QRCodePaymentFragment.newInstance(merchantInfo.getMerchantName(), merchantInfo.getQrCode(), merchantInfo.isPcqr()))
                                        .setReplace(true)
                                        .setAddToBackStack(true)
                                        .load();
                                mBinding.progressBar.setVisibility(View.GONE);
                            } else {
                                try {
                                    String[] splittedErrorBody = response.errorBody().string().split("\"");
                                    Toast.makeText(getActivity(), splittedErrorBody[7], Toast.LENGTH_LONG).show();
                                    mBinding.progressBar.setVisibility(View.GONE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MerchantInfo> call, Throwable t) {
                            Log.e("onFailure", "onFailure: " + t.getMessage());
                        }
                    });
                }
            }
        });
    }

}
package net.iGap.fragments.qrCodePayment.fragments;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.databinding.FragmentQRCodePaymentBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.PaymentFragment;
import net.iGap.fragments.qrCodePayment.viewModels.QRCodePaymentViewModel;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.model.payment.PaymentResult;
import net.iGap.model.qrCodePayment.Token;
import net.iGap.observers.interfaces.PaymentCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * After receive merchant information, this class is called
 * to indicate payment information and confirmation of it to
 * redirect process to payment
 */
public class QRCodePaymentFragment extends BaseFragment {

    public static final String MERCHANT_NAME = "name";
    public static final String MERCHANT_CODE = "code";
    public static final String MERCHANT_PCQR = "merchantPcqr";
    private FragmentQRCodePaymentBinding mBinding;
    private QRCodePaymentViewModel mViewModel;
    private Toolbar mToolbar;
    private String mMerchantName;
    private String mMerchantCode;
    private boolean mMerchantPcqr;

    public static QRCodePaymentFragment newInstance(String merchantName, String merchantCode, boolean merchantPcqr) {
        QRCodePaymentFragment fragment = new QRCodePaymentFragment();
        Bundle args = new Bundle();
        args.putString(MERCHANT_NAME, merchantName);
        args.putString(MERCHANT_CODE, merchantCode);
        args.putBoolean(MERCHANT_PCQR, merchantPcqr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMerchantName = getArguments().getString(MERCHANT_NAME);
            mMerchantCode = getArguments().getString(MERCHANT_CODE);
            mMerchantPcqr = getArguments().getBoolean(MERCHANT_PCQR);
        }
        mViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new QRCodePaymentViewModel(getActivity());
            }
        }).get(QRCodePaymentViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_q_r_code_payment, container, false);
        mBinding.setLifecycleOwner(this);
        mBinding.setViewModel(mViewModel);
        initViews();
        setObservers();
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
        mToolbar.setBackIcon(new BackDrawable(false));
        mToolbar.setListener(i -> {
            if (i == -1) {
                getActivity().onBackPressed();
            }
        });
        mBinding.toolbarContainer.addView(mToolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 64, Gravity.TOP));
    }

    private void initViews() {
        mBinding.acceptorName.setText(mMerchantName);
        mBinding.acceptorCode.setText(mMerchantCode);
        if (mMerchantPcqr) {
            mBinding.desireAmount.setVisibility(View.GONE);
            mBinding.desireAmountTextView.setVisibility(View.GONE);
        }
    }

    private void setObservers() {
        mViewModel.getConfirmButtonClick().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    if (!mMerchantPcqr && mBinding.desireAmount == null || !mMerchantPcqr && mBinding.desireAmount.getText().toString().length() == 0) {
                        Toast.makeText(getActivity(), R.string.you_have_not_specified_the_amount, Toast.LENGTH_SHORT).show();
                    } else if (!mMerchantPcqr && mBinding.desireAmount.getText().toString().length() != 0 &&Integer.parseInt(mBinding.desireAmount.getText().toString()) < 1000) {
                        Toast.makeText(getActivity(), R.string.the_amount_can_not_be_less_than_1000_rials, Toast.LENGTH_LONG).show();
                    } else {
                        mBinding.progressBar.setVisibility(View.VISIBLE);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("qr_code", mMerchantCode);
                        if (mMerchantPcqr) {
                            /**1000 is a default amount for pcqr true state. true pcqr means that the amount fill automatic and do not need to get amount from customer*/
                            jsonObject.addProperty("amount", 1000);
                        } else {
                            jsonObject.addProperty("amount", Integer.parseInt(mBinding.desireAmount.getText().toString()));
                        }
                        Call<Token> call = new RetrofitFactory().getPecQrRetrofit().getPaymentToken(jsonObject);
                        call.enqueue(new Callback<Token>() {
                            @Override
                            public void onResponse(Call<Token> call, Response<Token> response) {
                                if (response.isSuccessful()) {
                                    Token token = response.body();
                                    new HelperFragment(getActivity().getSupportFragmentManager(),
                                            PaymentFragment.getInstance(getActivity().getResources().getString(R.string.payment),
                                                    token.getToken(), new PaymentCallBack() {
                                                        @Override
                                                        public void onPaymentFinished(PaymentResult result) {
                                                        }
                                                    })).setReplace(false).setAddToBackStack(true).load();
                                    mBinding.progressBar.setVisibility(View.GONE);
                                } else {
                                    try {
                                        String[] splittedErrorBody = response.errorBody().string().split("\"");
                                        Toast.makeText(getActivity(), splittedErrorBody[splittedErrorBody.length - 2], Toast.LENGTH_LONG).show();
                                        mBinding.progressBar.setVisibility(View.GONE);
                                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Token> call, Throwable t) {
                            }
                        });
                    }
                }
            }
        });
    }
}
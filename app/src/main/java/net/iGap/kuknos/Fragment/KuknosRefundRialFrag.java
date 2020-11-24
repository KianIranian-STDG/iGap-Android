package net.iGap.kuknos.Fragment;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.kuknos.Model.Parsian.KuknosAsset;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;
import net.iGap.kuknos.Model.Parsian.KuknosRefundModel;
import net.iGap.kuknos.viewmodel.KuknosRefundVM;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmKuknos;

import java.text.DecimalFormat;

import io.realm.Realm;


public class KuknosRefundRialFrag extends BaseAPIViewFrag<KuknosRefundVM> {
    private static final String TAG = "KuknosEquivalentRialFra";
    private Button submit;
    private TextView txtMaxAmount, txtMinAmount, txtFeeFixed, txtSellRate, txtMaxSell, txtIBAN;
    private TextView txtPeymanCount, txtFinalFeeFixed, txtTotalPeyman, txtTotalPrice;
    private KuknosRefundModel refundModel;
    private KuknosBalance balance;
    private ProgressBar refundProgress, mainProgress;
    private ConstraintLayout fragKRRConstrain;
    private String assetCode;
    private ScrollView myScrollView;
    private ConstraintLayout scrollChildViews;
    private DecimalFormat df;
    float finalFee;
    float totalPeyman;
    int totalPrice;
    int maxRefund;
    int minRefund;
    float fee;
    int sellRate;
    float maxPeymanRefund;
    float refundRequestCount;
    private float minBalance;
    boolean isPercentMode;

    public static KuknosRefundRialFrag newInstance() {
        KuknosRefundRialFrag fragment = new KuknosRefundRialFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewModel = ViewModelProviders.of(this).get(KuknosRefundVM.class);
        refundModel = new KuknosRefundModel();
        balance = new KuknosBalance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kuknos_equivalent_rial, container, false);
        initViews(view);
        df = new DecimalFormat("#,##0.00");
        if (getArguments() != null) {
            assetCode = getArguments().getString("assetType");
        }
        viewModel.getRefundInfoFromServer(assetCode);
        viewModel.getPMNAssetData(assetCode);
        viewModel.getPMNMinBalance();


        String iBan = DbManager.getInstance().doRealmTask(realm -> {
            RealmKuknos realmKuknos = realm.where(RealmKuknos.class).findFirst();
            if (realmKuknos != null && realmKuknos.getIban() != null) {
                return realmKuknos.getIban();
            } else {
                return null;
            }
        });
        txtIBAN.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(iBan) : iBan);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtPeymanCount.setTypeface(getActivity().getResources().getFont(R.font.main_font));
        } else {
            ResourcesCompat.getFont(getActivity(), R.font.main_font);
        }

        txtPeymanCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                View lastChild = scrollChildViews.getChildAt(18);
                int bottom = lastChild.getBottom() + myScrollView.getPaddingBottom() + 10;
                int sy = myScrollView.getScrollY();
                int sh = myScrollView.getHeight();
                int delta = bottom - (sy + sh);
                myScrollView.smoothScrollBy(0, delta);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    refundRequestCount = Float.parseFloat(s.toString());

                    float peymanCount = Float.parseFloat(s.toString());
                    if (peymanCount >= refundModel.getMinRefund() && peymanCount <= refundModel.getMaxRefund()) {


                        if (isPercentMode) {
                            finalFee = peymanCount * fee;
                        } else {
                            finalFee = fee;
                        }

                        totalPeyman = finalFee + peymanCount;
                        totalPrice = (int) (peymanCount * sellRate);

                        txtFinalFeeFixed.setText(
                                HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Float.valueOf(finalFee)))
                                        : df.format(Float.valueOf(finalFee)));

                        txtTotalPeyman.setText(
                                HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Float.valueOf(totalPeyman)))
                                        : df.format(Float.valueOf(totalPeyman)));

                        txtTotalPrice.setText(
                                HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Integer.valueOf(totalPrice)))
                                        : df.format(Integer.valueOf(totalPrice)));
                    }
                } else {
                    txtFinalFeeFixed.setText("");
                    txtTotalPeyman.setText("");
                    txtTotalPrice.setTag("");
                }
            }
        });

        submit.setOnClickListener(v -> {

            float peymanCount;

            if (!txtPeymanCount.getText().toString().isEmpty() && txtPeymanCount.getText() != null) {
                peymanCount = refundRequestCount;
                if (peymanCount >= refundModel.getMinRefund() && peymanCount <= refundModel.getMaxRefund()) {

                    if (peymanCount <= maxPeymanRefund) {
                        showRefundDialog(assetCode, peymanCount, finalFee, totalPrice);

                    } else {
                        Toast.makeText(_mActivity, R.string.payman_token_not_enough, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(_mActivity, R.string.payman_refund_limitation_error, Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(), R.string.enter_pmn_count, Toast.LENGTH_SHORT).show();
            }


        });

        onMinBalance();
        onRefundDataReceived();
        onUserAssetsReceived();
        onRefundSuccess();
        onRefundButtonProgress();
        onNetworkCallSuccess();
        onNetworkCallError();
        onRefNoReceive();
        return view;
    }

    private void showRefundDialog(String assetCode, float assetCount, float feeFixed, long amount) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.kuknos_refund_submit_dialog, null);
        TextView edtAssetCode, edtAssetCount, edtFeeFixed, edtAmount;
        edtAssetCode = dialogView.findViewById(R.id.dialog_refund_asset);
        edtAssetCount = dialogView.findViewById(R.id.dialog_refund_count);
        edtFeeFixed = dialogView.findViewById(R.id.dialog_fixed_fee);
        edtAmount = dialogView.findViewById(R.id.dialog_amount);
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .customView(dialogView, true)
                .widgetColor(new Theme().getPrimaryColor(getContext()))
                .build();
        dialogView.findViewById(R.id.kuknos_refund_dialog_submit).setOnClickListener(v -> {
            viewModel.requestForVirtualRefund(String.valueOf(totalPeyman), totalPrice, finalFee);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.kuknos_refund_dialog_cancel).setOnClickListener(v -> dialog.dismiss());
        edtAssetCode.setText(assetCode);

        edtAssetCount.setText(
                HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Float.valueOf(assetCount)))
                        : df.format(Float.valueOf(assetCount)));

        edtFeeFixed.setText(
                HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Float.valueOf(feeFixed)))
                        : df.format(Float.valueOf(feeFixed)));
        edtAmount.setText(
                HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Long.valueOf(amount)))
                        : df.format(Long.valueOf(amount)));
        dialog.show();
    }

    private void onRefundDataReceived() {
        viewModel.getRefundData().observe(getViewLifecycleOwner(), kuknosRefundModel -> {
            if (kuknosRefundModel != null) {

                maxRefund = kuknosRefundModel.getMaxRefund();
                minRefund = kuknosRefundModel.getMinRefund();

                sellRate = kuknosRefundModel.getSellRate();
                String strSellRate = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Integer.valueOf(sellRate)))
                        : df.format(Integer.valueOf(sellRate));


                if (kuknosRefundModel.getFeeFixed() != 0) {
                    fee = kuknosRefundModel.getFeeFixed();
                    isPercentMode = false;
                } else {
                    fee = kuknosRefundModel.getFeePercent();
                    isPercentMode = true;
                }

                String max = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(maxRefund))
                        : String.valueOf(maxRefund);

                String min = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(minRefund))
                        : String.valueOf(minRefund);

                String strFeeFixed = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(fee))
                        : String.valueOf(fee);

                txtMaxAmount.setText(max);
                txtMinAmount.setText(min);
                txtSellRate.setText(strSellRate);

                if (isPercentMode) {
                    txtFeeFixed.setText(strFeeFixed + " %");
                } else {
                    txtFeeFixed.setText(strFeeFixed);
                }

                refundModel.setFeeFixed(kuknosRefundModel.getFeeFixed());
                refundModel.setMaxRefund(kuknosRefundModel.getMaxRefund());
                refundModel.setMinRefund(kuknosRefundModel.getMinRefund());


            }
        });
    }

    private void onRefundSuccess() {
        viewModel.getIsRefundSuccess().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                Toast.makeText(_mActivity, R.string.refund_done, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(_mActivity, R.string.refund_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRefNoReceive() {
        viewModel.getRefNo().observe(getViewLifecycleOwner(), refNo -> {
            if (refNo != 0) {
                new HelperFragment(getActivity().getSupportFragmentManager(), KuknosReceiptFrag.newInstance(refNo)).setReplace(false).load();
            }
        });
    }

    private void onRefundButtonProgress() {
        viewModel.getRefundProgress().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                refundProgress.setVisibility(View.VISIBLE);
                submit.setEnabled(false);
            } else {
                refundProgress.setVisibility(View.INVISIBLE);
                submit.setEnabled(true);
            }
        });
    }

    private void onUserAssetsReceived() {
        viewModel.getBalanceData().observe(getViewLifecycleOwner(), (Observer<KuknosBalance>) kuknosBalance -> {
            if (kuknosBalance != null) {

                maxPeymanRefund = (float) (Float.parseFloat(kuknosBalance.getAssets().get(0).getBalance()) - minBalance);

                if (maxPeymanRefund < 0) {
                    maxPeymanRefund = 0;
                }

                String maxRefund = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Float.valueOf(maxPeymanRefund)))
                        : df.format(Float.valueOf(maxPeymanRefund));

                txtMaxSell.setText(maxRefund);
                balance.setAssets(kuknosBalance.getAssets());

            }
        });
    }

    private void onNetworkCallSuccess() {
        viewModel.getRequestsSuccess().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    fragKRRConstrain.setVisibility(View.VISIBLE);
                    mainProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void onNetworkCallError() {
        viewModel.getRequestsError().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mainProgress.setVisibility(View.INVISIBLE);
                HelperError.showSnackMessage(getString(R.string.network_error), false);
            }
        });
    }

    private void onMinBalance() {
        viewModel.getMinBalance().observe(getViewLifecycleOwner(), aFloat -> {
            if (aFloat != null) {
                minBalance = aFloat;
            }
        });
    }

    private void initViews(View view) {
        submit = view.findViewById(R.id.fragKuknosRialSubmit);
        txtMaxAmount = view.findViewById(R.id.textView28);
        txtMinAmount = view.findViewById(R.id.textView20);
        txtFeeFixed = view.findViewById(R.id.textView29);
        txtSellRate = view.findViewById(R.id.textView21);
        txtMaxSell = view.findViewById(R.id.textView31);
        txtIBAN = view.findViewById(R.id.textView30);
        txtPeymanCount = view.findViewById(R.id.editText5);
        txtFinalFeeFixed = view.findViewById(R.id.editText2);
        txtTotalPeyman = view.findViewById(R.id.editText3);
        txtTotalPrice = view.findViewById(R.id.editText4);
        refundProgress = view.findViewById(R.id.progressRefund);
        fragKRRConstrain = view.findViewById(R.id.fragKRRConstrain);
        mainProgress = view.findViewById(R.id.mainProgress);
        myScrollView = view.findViewById(R.id.kuknos_rial_rootview);
        scrollChildViews = view.findViewById(R.id.fragKRRConstrain);
    }
}
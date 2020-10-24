package net.iGap.kuknos.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperError;
import net.iGap.kuknos.Model.Parsian.KuknosAsset;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;
import net.iGap.kuknos.Model.Parsian.KuknosRefundModel;
import net.iGap.kuknos.viewmodel.KuknosRefundVM;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.realm.RealmKuknos;

import io.realm.Realm;
import ir.radsense.raadcore.widget.BottomSheet;


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

    public static KuknosRefundRialFrag newInstance() {
        KuknosRefundRialFrag fragment = new KuknosRefundRialFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (getArguments() != null) {
            assetCode = getArguments().getString("assetType");
        }
        viewModel.getRefundInfoFromServer(assetCode);
        viewModel.getPMNAssetData(assetCode);
        viewModel.getAccountAssets();


        String iBan = DbManager.getInstance().doRealmTask(new DbManager.RealmTaskWithReturn<String>() {
            @Override
            public String doTask(Realm realm) {
                RealmKuknos realmKuknos = realm.where(RealmKuknos.class).findFirst();
                if (realmKuknos != null && realmKuknos.getIban() != null) {
                    return realmKuknos.getIban();
                } else {
                    return null;
                }
            }
        });
        txtIBAN.setText(iBan);

        txtPeymanCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    float peymanCount = Float.parseFloat(s.toString());
                    if (peymanCount >= refundModel.getMinRefund() && peymanCount <= refundModel.getMaxRefund()) {
                        txtPeymanCount.setTextColor(Color.BLACK);
                        float finalFee = peymanCount * (refundModel.getFeeFixed() / 100.0f);
                        float totalPeyman = finalFee + peymanCount;
                        int totalPrice = (int) (peymanCount * Integer.parseInt(txtSellRate.getText().toString()));

                        txtFinalFeeFixed.setText("" + finalFee);
                        txtTotalPeyman.setText("" + totalPeyman);
                        txtTotalPrice.setText("" + totalPrice);

                    } else {
                        txtPeymanCount.setTextColor(Color.RED);
                    }
                } else {
                    txtFinalFeeFixed.setText("");
                    txtTotalPeyman.setText("");
                    txtTotalPrice.setTag("");
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float maxPeymanRefund = 0;
                float peymanCount = 0;
                if (!txtPeymanCount.getText().toString().isEmpty() && txtPeymanCount.getText().toString() != null) {
                    peymanCount = Float.parseFloat(txtPeymanCount.getText().toString());
                    maxPeymanRefund = (float) (Float.parseFloat(balance.getAssets().get(0).getBalance()) - 1.5);
                    if (peymanCount >= refundModel.getMinRefund() && peymanCount <= refundModel.getMaxRefund()) {

                        if (peymanCount <= maxPeymanRefund) {
                            showRefundDialog(assetCode, peymanCount, Float.parseFloat(txtFinalFeeFixed.getText().toString()),Long.parseLong(txtTotalPrice.getText().toString()));

                        } else {
                            Toast.makeText(_mActivity, "Peyman Token in not enough", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(_mActivity, "You have not complied with the token limit.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Please enter PMN count", Toast.LENGTH_SHORT).show();
                }


            }
        });

        onRefundDataReceived();
        onAssetDataReceived();
        onUserAssetsReceived();
        onRefundSuccess();
        onRefundButtonProgress();
        onNetworkCallSuccess();
        onNetworkCallError();

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
            viewModel.requestForVirtualRefund(txtTotalPeyman.getText().toString(), Integer.parseInt(txtTotalPrice.getText().toString()), Float.parseFloat(txtFinalFeeFixed.getText().toString()));
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.kuknos_refund_dialog_cancel).setOnClickListener(v -> dialog.dismiss());
        edtAssetCode.setText(assetCode);
        edtAssetCount.setText("" + assetCount);
        edtFeeFixed.setText("" + feeFixed);
        edtAmount.setText("" + amount);
        dialog.show();
    }
    private void onRefundDataReceived() {
        viewModel.getRefundData().observe(getViewLifecycleOwner(), new Observer<KuknosRefundModel>() {
            @Override
            public void onChanged(KuknosRefundModel kuknosRefundModel) {
                if (kuknosRefundModel != null) {
                    txtMaxAmount.setText("" + kuknosRefundModel.getMaxRefund());
                    txtMinAmount.setText("" + kuknosRefundModel.getMinRefund());
                    txtFeeFixed.setText("" + kuknosRefundModel.getFeeFixed());

                    refundModel.setFeeFixed(kuknosRefundModel.getFeeFixed());
                    refundModel.setMaxRefund(kuknosRefundModel.getMaxRefund());
                    refundModel.setMinRefund(kuknosRefundModel.getMinRefund());


                }
            }
        });
    }

    private void onRefundSuccess() {
        viewModel.getIsRefundSuccess().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(_mActivity, "Refund done successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void onRefundButtonProgress() {
        viewModel.getRefundProgress().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    refundProgress.setVisibility(View.VISIBLE);
                } else {
                    refundProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void onAssetDataReceived() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), new Observer<KuknosAsset>() {
            @Override
            public void onChanged(KuknosAsset kuknosAsset) {
                if (kuknosAsset != null) {
                    txtSellRate.setText("" + kuknosAsset.getAssets().get(0).getSellRate());

                }
            }
        });
    }

    private void onUserAssetsReceived() {
        viewModel.getBalanceData().observe(getViewLifecycleOwner(), new Observer<KuknosBalance>() {
            @Override
            public void onChanged(KuknosBalance kuknosBalance) {
                if (kuknosBalance != null) {
                    float maxPeymanRefund = (float) (Float.parseFloat(kuknosBalance.getAssets().get(0).getBalance()) - 1.5);
                    txtMaxSell.setText("" + maxPeymanRefund);
                    balance.setAssets(kuknosBalance.getAssets());

                }
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
    }
}
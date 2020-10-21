package net.iGap.kuknos.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.fragments.BaseFragment;
import net.iGap.kuknos.Model.Parsian.KuknosAsset;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;
import net.iGap.kuknos.Model.Parsian.KuknosRefundModel;
import net.iGap.kuknos.viewmodel.KuknosRefundVM;
import net.iGap.module.StructWallpaper;
import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmKuknos;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.realm.Realm;
import retrofit2.Call;


public class KuknosEquivalentRialFrag extends BaseAPIViewFrag<KuknosRefundVM> {
    private static final String TAG = "KuknosEquivalentRialFra";
    private Button submit;
    private TextView txtMaxAmount, txtMinAmount, txtFeeFixed, txtSellRate, txtMaxSell, txtIBAN;
    private TextView txtPeymanCount, txtFinalFeeFixed, txtTotalPeyman, txtTotalPrice;
    private KuknosRefundModel refundModel;
    private KuknosBalance balance;
    private ProgressBar refundProgress;

    public static KuknosEquivalentRialFrag newInstance() {
        KuknosEquivalentRialFrag fragment = new KuknosEquivalentRialFrag();
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
        viewModel.getRefundInfoFromServer();
        viewModel.getPMNAssetData();
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
                if (!txtPeymanCount.getText().toString().isEmpty() && txtPeymanCount.getText() != null) {
                    peymanCount  = Float.parseFloat(txtPeymanCount.getText().toString());
                    maxPeymanRefund = (float) (Float.parseFloat(balance.getAssets().get(0).getBalance()) - 1.5);
                }

                if ( peymanCount <= maxPeymanRefund ) {

                    viewModel.requestForVirtualRefund(txtTotalPeyman.getText().toString(),Integer.parseInt(txtTotalPrice.getText().toString()), Float.parseFloat(txtFinalFeeFixed.getText().toString()));

                } else {
                    Toast.makeText(_mActivity, "Peyman Token in not enough", Toast.LENGTH_SHORT).show();
                }
            }
        });

        onRefundDataReceived();
        onAssetDataReceived();
        onUserAssetsReceived();
        onProgress();

        return view;
    }

    private void onRefundDataReceived() {
        viewModel.getRefundData().observe(getActivity(), new Observer<KuknosRefundModel>() {
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

    private void onProgress() {
        viewModel.getRefundProgress().observe(getActivity(), new Observer<Boolean>() {
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
        viewModel.getAssetData().observe(getActivity(), new Observer<KuknosAsset>() {
            @Override
            public void onChanged(KuknosAsset kuknosAsset) {
                if (kuknosAsset != null) {
                    txtSellRate.setText("" + kuknosAsset.getAssets().get(0).getSellRate());

                }
            }
        });
    }

    private void onUserAssetsReceived() {
        viewModel.getBalanceData().observe(getActivity(), new Observer<KuknosBalance>() {
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
    }
}
package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.fragments.BaseFragment;
import net.iGap.kuknos.Model.Parsian.KuknosAsset;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;
import net.iGap.kuknos.Model.Parsian.KuknosRefundModel;
import net.iGap.kuknos.viewmodel.KuknosRefundVM;
import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmKuknos;

import io.realm.Realm;


public class KuknosEquivalentRialFrag extends BaseAPIViewFrag<KuknosRefundVM> {
    private Button submit;
    private TextView txtMaxAmount, txtMinAmount, txtFeeFixed, txtSellRate, txtMaxSell, txtIBAN;

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

        RealmKuknos realmKuknos = new RealmKuknos();
        txtIBAN.setText(realmKuknos.getIban());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        onRefundDataReceived();
        onAssetDataReceived();
        onUserAssetsReceived();
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
                    txtMaxSell.setText("" + kuknosBalance.getAssets().get(0).getLimit());
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
    }
}
package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperToolbar;
import net.iGap.kuknos.viewmodel.KuknosReceiptVM;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmKuknos;

import io.realm.Realm;


public class KuknosReceiptFrag extends BaseAPIViewFrag<KuknosReceiptVM> {

    private static final String REFUND_NO = "refundNo";
    private TextView shebaNumber;
    private TextView token;
    private TextView amount;
    private TextView refundAddress;
    private TextView typeRefund;
    private TextView state;
    private TextView hash;
    private TextView dateApply;
    private TextView error;
    private Button returnTokenDigital;
    private ProgressBar progressBar;
    private View rootView;
    private TextView shebaNumberSide;
    private TextView tokenSide;
    private TextView amountSide;
    private TextView refundAddressSide;
    private TextView typeRefundSide;
    private TextView stateSide;
    private TextView hashSide;
    private TextView dateApplySide;
    private ImageView divider1;
    private ImageView divider2;
    private ImageView divider3;
    private ImageView divider4;
    private ImageView divider5;
    private ImageView divider6;
    private ImageView divider7;
    private LinearLayout toolbarLayout;
    private String iban;

    public static KuknosReceiptFrag newInstance(int refundNo) {
        KuknosReceiptFrag fragment = new KuknosReceiptFrag();
        Bundle args = new Bundle();
        args.putInt(REFUND_NO, refundNo);
        fragment.setArguments(args);
        return fragment;
    }

    public static KuknosReceiptFrag newInstance() {
        KuknosReceiptFrag fragment = new KuknosReceiptFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iban = DbManager.getInstance().doRealmTask(new DbManager.RealmTaskWithReturn<String>() {

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
        viewModel = ViewModelProviders.of(this).get(KuknosReceiptVM.class);
        viewModel.getUserRefundDetail(getArguments().getInt(REFUND_NO));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kuknos_receipt, container, false);
        initView(view);
        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setDefaultTitle(getString(R.string.kuknos_receipt_bill))
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);
        toolbarLayout = view.findViewById(R.id.fragKuknosReceiptToolbar);
        toolbarLayout.addView(mHelperToolbar.getView());
        returnTokenDigital.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        returnTokenDigital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStackFragment();
                popBackStackFragment();
            }
        });
        onUserRefundInfoObserver();
        onResponseState();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initView(View view) {
        rootView = view.findViewById(R.id.kuknos_receipt);
        progressBar = view.findViewById(R.id.kuknos_receipt_progressbar);
        shebaNumber = view.findViewById(R.id.kuknos_receipt_sheba_number);
        token = view.findViewById(R.id.kuknos_receipt_token);
        amount = view.findViewById(R.id.kuknos_receipt_amount);
        refundAddress = view.findViewById(R.id.kuknos_receipt_refund_address);
        typeRefund = view.findViewById(R.id.kuknos_receipt_type_refund);
        state = view.findViewById(R.id.kuknos_receipt_state);
        hash = view.findViewById(R.id.kuknos_receipt_hash);
        dateApply = view.findViewById(R.id.kuknos_receipt_date_apply);
        error = view.findViewById(R.id.kuknos_receipt_error_txt);
        returnTokenDigital = view.findViewById(R.id.fragKuknosReceiptSubmit);
        shebaNumberSide = view.findViewById(R.id.kuknos_receipt_side_sheba_number);
        tokenSide = view.findViewById(R.id.kuknos_receipt_side_token);
        amountSide = view.findViewById(R.id.kuknos_receipt_side_amount);
        refundAddressSide = view.findViewById(R.id.kuknos_receipt_refund_side_address);
        typeRefundSide = view.findViewById(R.id.kuknos_receipt_type_side_refund);
        stateSide = view.findViewById(R.id.kuknos_receipt_state_side);
        hashSide = view.findViewById(R.id.kuknos_receipt_hash_side);
        dateApplySide = view.findViewById(R.id.kuknos_receipt_date_apply_side);
        divider1 = view.findViewById(R.id.divider1);
        divider2 = view.findViewById(R.id.divider2);
        divider3 = view.findViewById(R.id.divider3);
        divider4 = view.findViewById(R.id.divider4);
        divider5 = view.findViewById(R.id.divider5);
        divider6 = view.findViewById(R.id.divider6);
        divider7 = view.findViewById(R.id.divider7);
    }

    private void vidisbleViews() {
        toolbarLayout.setVisibility(View.VISIBLE);
        shebaNumber.setVisibility(View.VISIBLE);
        token.setVisibility(View.VISIBLE);
        amount.setVisibility(View.VISIBLE);
        refundAddress.setVisibility(View.VISIBLE);
        typeRefund.setVisibility(View.VISIBLE);
        state.setVisibility(View.VISIBLE);
        hash.setVisibility(View.VISIBLE);
        dateApply.setVisibility(View.VISIBLE);
        shebaNumberSide.setVisibility(View.VISIBLE);
        tokenSide.setVisibility(View.VISIBLE);
        amountSide.setVisibility(View.VISIBLE);
        refundAddressSide.setVisibility(View.VISIBLE);
        typeRefundSide.setVisibility(View.VISIBLE);
        stateSide.setVisibility(View.VISIBLE);
        hashSide.setVisibility(View.VISIBLE);
        dateApplySide.setVisibility(View.VISIBLE);
        returnTokenDigital.setVisibility(View.VISIBLE);
        divider1.setVisibility(View.VISIBLE);
        divider2.setVisibility(View.VISIBLE);
        divider3.setVisibility(View.VISIBLE);
        divider4.setVisibility(View.VISIBLE);
        divider5.setVisibility(View.VISIBLE);
        divider6.setVisibility(View.VISIBLE);
        divider7.setVisibility(View.VISIBLE);
    }

    private void onUserRefundInfoObserver() {
        viewModel.getRefundInfo().observe(getViewLifecycleOwner(), refundResponse -> {
            progressBar.setVisibility(View.GONE);
            if (refundResponse != null) {
                rootView.setVisibility(View.VISIBLE);
                vidisbleViews();
                returnTokenDigital.setEnabled(true);
                typeRefund.setText(refundResponse.getRefundType());
                hash.setText(refundResponse.getHash());
                state.setText(refundResponse.getSettlementStatus());
                amount.setText(refundResponse.getAmount());
                token.setText(refundResponse.getAssetCode());
                dateApply.setText(refundResponse.getTransactionDate());
                shebaNumber.setText(iban);
                refundAddress.setText(viewModel.getPanelRepo().getUserRepo().getAccountID());
            } else {
                error.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onResponseState() {
        viewModel.getResponseMessage().observe(getViewLifecycleOwner(), infoResponse -> {
            progressBar.setVisibility(View.GONE);

            if (infoResponse.equals("true")) {
                rootView.setVisibility(View.VISIBLE);
                vidisbleViews();
                returnTokenDigital.setEnabled(true);
            } else {
                error.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), infoResponse, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
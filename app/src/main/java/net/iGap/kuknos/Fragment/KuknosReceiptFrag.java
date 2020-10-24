package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.viewmodel.KuknosEditInfoVM;
import net.iGap.kuknos.viewmodel.KuknosReceiptVM;
import net.iGap.module.AndroidUtils;


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
    private ProgressBar progressBar;
    private View rootView;

    public static KuknosReceiptFrag newInstance(String refundNo) {
        KuknosReceiptFrag fragment = new KuknosReceiptFrag();
        Bundle args = new Bundle();
        args.putString(REFUND_NO, refundNo);
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
        viewModel = ViewModelProviders.of(this).get(KuknosReceiptVM.class);
        viewModel.getUserRefundDetail(86);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kuknos_receipt, container, false);
        initView(view);
        rootView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
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
    }

    private void onUserRefundInfoObserver() {
        viewModel.getRefundInfo().observe(getViewLifecycleOwner(), refundResponse -> {
            progressBar.setVisibility(View.GONE);
            if (refundResponse != null) {
                rootView.setVisibility(View.VISIBLE);
                typeRefund.setText(refundResponse.getRefundType());
                hash.setText(refundResponse.getHash());
                state.setText(refundResponse.getSettlementStatus());
                amount.setText(refundResponse.getAmount());
                token.setText(refundResponse.getAssetCode());
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
            } else {
                rootView.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), infoResponse, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package net.iGap.fragments.mplTranaction;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;
import java.util.List;

public class MplTransactionInfoFragment extends BaseFragment implements ToolbarListener {
    private View rootView;
    private String token;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MplTransactionInfoViewModel viewModel;
    private MplTransactionInfoAdapter adapter;
    private TextView statusTv;
    private TextView dataTv;
    private TextView timeTv;
    private TextView statusIv;
    private View statusRootView;
    private View rvContainer;
    private RecyclerView recyclerView;
    private TextView emptyView;

    public static MplTransactionInfoFragment getInstance(String token) {
        MplTransactionInfoFragment mplTransactionInfoFragment = new MplTransactionInfoFragment();
        mplTransactionInfoFragment.token = token;
        return mplTransactionInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mpl_trancaction_info, container, false);
        viewModel = new MplTransactionInfoViewModel();
        adapter = new MplTransactionInfoAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();

        viewModel.getTransactionInfo(token);

        viewModel.getTransactionInfoLiveData().observe(getViewLifecycleOwner(), mplTransaction -> {
            List<MilTransActionStruct> data = new ArrayList<>();

            if (mplTransaction != null) {
                switch (mplTransaction.getType()) {
                    case CARD_TO_CARD:
                        ProtoGlobal.MplTransaction.CardToCard transactionCardToCard = mplTransaction.getCardtocard();
                        data = new ArrayList<>();

                        setStatus(transactionCardToCard.getStatus());
                        setData(transactionCardToCard.getRequestDateTime());

                        data.add(new MilTransActionStruct("BankName", transactionCardToCard.getBankName()));
                        data.add(new MilTransActionStruct("OwnerName", transactionCardToCard.getCardOwnerName()));
                        data.add(new MilTransActionStruct("DestBank", transactionCardToCard.getDestBankName()));
                        data.add(new MilTransActionStruct("DestCard", transactionCardToCard.getDestCardNumber()));
                        data.add(new MilTransActionStruct("SourceCard", transactionCardToCard.getSourceCardNumber()));
                        data.add(new MilTransActionStruct("Amount", String.valueOf(transactionCardToCard.getAmount())));
                        data.add(new MilTransActionStruct("OrderId", String.valueOf(transactionCardToCard.getOrderId())));
                        data.add(new MilTransActionStruct("RRN", String.valueOf(transactionCardToCard.getRRN())));
                        data.add(new MilTransActionStruct("TraceNo", String.valueOf(transactionCardToCard.getTraceNo())));

                        break;
                    case TOPUP:
                        ProtoGlobal.MplTransaction.Topup transactionTopUp = mplTransaction.getTopup();
                        data = new ArrayList<>();

                        setStatus(transactionTopUp.getStatus());

                        data.add(new MilTransActionStruct("Merchant name", transactionTopUp.getMerchantName()));
                        data.add(new MilTransActionStruct("Card Number", transactionTopUp.getCardNumber()));
                        data.add(new MilTransActionStruct("StatusDescription", transactionTopUp.getStatusDescription()));
                        data.add(new MilTransActionStruct("TerminalNo", String.valueOf(transactionTopUp.getTerminalNo())));
                        data.add(new MilTransActionStruct("TraceNo", String.valueOf(transactionTopUp.getTraceNo())));
                        data.add(new MilTransActionStruct("Amount", String.valueOf(transactionTopUp.getAmount())));
                        data.add(new MilTransActionStruct("MobileNumber", String.valueOf(transactionTopUp.getChargeMobileNumber())));
                        data.add(new MilTransActionStruct("OrderId", String.valueOf(transactionTopUp.getOrderId())));
                        data.add(new MilTransActionStruct("RRN", String.valueOf(transactionTopUp.getRRN())));


                        break;
                    case SALES:
                        ProtoGlobal.MplTransaction.Sales transactionSales = mplTransaction.getSales();
                        data = new ArrayList<>();

                        setStatus(transactionSales.getStatus());

                        data.add(new MilTransActionStruct("MerchantName", transactionSales.getMerchantName()));
                        data.add(new MilTransActionStruct("CardNumber", transactionSales.getCardNumber()));
                        data.add(new MilTransActionStruct("TerminalNo", String.valueOf(transactionSales.getTerminalNo())));
                        data.add(new MilTransActionStruct("TraceNo", String.valueOf(transactionSales.getTraceNo())));
                        data.add(new MilTransActionStruct("Amount", String.valueOf(transactionSales.getAmount())));
                        data.add(new MilTransActionStruct("OrderId", String.valueOf(transactionSales.getOrderId())));
                        data.add(new MilTransActionStruct("Amount", String.valueOf(transactionSales.getAmount())));
                        data.add(new MilTransActionStruct("RRN", String.valueOf(transactionSales.getRRN())));

                        break;
                    case BILL:
                        ProtoGlobal.MplTransaction.Bill transactionBill = mplTransaction.getBill();
                        data = new ArrayList<>();

                        setStatus(transactionBill.getStatus());

                        data.add(new MilTransActionStruct("BillId", transactionBill.getBillId()));
                        data.add(new MilTransActionStruct("BillType", transactionBill.getBillType()));
                        data.add(new MilTransActionStruct("CardNumber", transactionBill.getCardNumber()));
                        data.add(new MilTransActionStruct("MerchantName", transactionBill.getMerchantName()));
                        data.add(new MilTransActionStruct("PayId", transactionBill.getPayId()));
                        data.add(new MilTransActionStruct("StatusDescription", transactionBill.getStatusDescription()));
                        data.add(new MilTransActionStruct("Amount", String.valueOf(transactionBill.getAmount())));
                        data.add(new MilTransActionStruct("OrderId", String.valueOf(transactionBill.getOrderId())));
                        data.add(new MilTransActionStruct("RRN", String.valueOf(transactionBill.getRRN())));
                        data.add(new MilTransActionStruct("TerminalNo", String.valueOf(transactionBill.getTerminalNo())));
                        data.add(new MilTransActionStruct("TraceNo", String.valueOf(transactionBill.getTraceNo())));

                        break;
                }
            }
            adapter.setData(data);

            statusRootView.setVisibility(View.VISIBLE);
            rvContainer.setVisibility(View.VISIBLE);

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(300);
            rvContainer.setAnimation(fadeIn);
            statusRootView.setAnimation(fadeIn);

        });

        viewModel.getErrorTransActionInfoLiveData().observe(getViewLifecycleOwner(), isInvalid -> {
            if (isInvalid != null && isInvalid)
                emptyView.setVisibility(View.VISIBLE);
            else
                emptyView.setVisibility(View.GONE);
        });

        viewModel.getProgressLiveData().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null)
                swipeRefreshLayout.setRefreshing(progress);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.getTransactionInfo(token));


    }

    private void setupViews() {
        LinearLayout toolBarContainer = rootView.findViewById(R.id.ll_mplTransactionInfo_toolBar);
        swipeRefreshLayout = rootView.findViewById(R.id.sl_mplTransactionInfo);
        recyclerView = rootView.findViewById(R.id.rv_mplTransActionInfo);
        rvContainer = rootView.findViewById(R.id.cl_mplTransactionInfo_rv);
        statusTv = rootView.findViewById(R.id.tv_mplTransactionInfo_stats);
        dataTv = rootView.findViewById(R.id.tv_mplTransactionInfo_data);
        timeTv = rootView.findViewById(R.id.tv_mplTransactionInfo_time);
        statusIv = rootView.findViewById(R.id.fv_mplTransactionInfo_status);
        statusRootView = rootView.findViewById(R.id.cv_mplTransactionInfo);
        emptyView = rootView.findViewById(R.id.tv_mplTransactionInfo_emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(G.fragmentActivity)
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle("سوابق تراکنش")
                .setLeftIcon(R.string.back_icon);

        toolBarContainer.addView(toolbar.getView());
    }

    private void setStatus(int status) {
        if (status == 0)
            successfulStatus();
        else
            unsuccessfulStatus();
    }

    private void successfulStatus() {
        statusTv.setText("وضعیت \n موفق");
        statusTv.setTextColor(getResources().getColor(R.color.green));
        statusIv.setTextColor(getResources().getColor(R.color.green));
    }

    private void unsuccessfulStatus() {
        statusTv.setText("وضعیت \n ناموفق");
        statusTv.setTextColor(getResources().getColor(R.color.red));
        statusIv.setTextColor(getResources().getColor(R.color.red));
        statusIv.setText(getResources().getString(R.string.error_icon));
    }

    private void setData(long data) {
        dataTv.setText(HelperCalander.getTimeForMainRoom(data));
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    public class MilTransActionStruct {
        private String title;
        private String data;

        public MilTransActionStruct(String title, String data) {
            this.title = title;
            this.data = data;
        }

        public String getTitle() {
            return title;
        }

        public String getData() {
            return data;
        }
    }
}

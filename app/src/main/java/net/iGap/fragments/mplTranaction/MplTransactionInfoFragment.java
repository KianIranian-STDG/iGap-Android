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
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;
import java.util.List;

public class MplTransactionInfoFragment extends BaseFragment {
    private View rootView;
    private String token;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MplTransactionInfoViewModel viewModel;
    private MplTransActionInfoAdapter adapter;
    private TextView statusTv;
    private TextView dataTv;
    private TextView timeTv;
    private TextView statusIv;
    private View statusRootView;

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
        adapter = new MplTransActionInfoAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();

        viewModel.getTransactionInfo(token);

        viewModel.getTransactionInfoLiveData().observe(getViewLifecycleOwner(), mplTransaction -> {
            List<String> strings = new ArrayList<>();
            statusRootView.setVisibility(View.VISIBLE);

            if (mplTransaction != null) {

                switch (mplTransaction.getType()) {
                    case CARD_TO_CARD:
                        ProtoGlobal.MplTransaction.CardToCard transactionCardtocard = mplTransaction.getCardtocard();

                        break;
                    case TOPUP:
                        ProtoGlobal.MplTransaction.Topup transactionTopUp = mplTransaction.getTopup();

                        switch (transactionTopUp.getStatus()) {
                            case 0:
                                successfulStatus();
                                break;
                            case 1:
                                unsuccessfulStatus();
                                break;
                            case 2:
                                pendingStatus();
                                break;
                            case 3:

                                break;
                        }

                        strings.add(transactionTopUp.getMerchantName());
                        strings.add(transactionTopUp.getCardNumber());
                        strings.add(transactionTopUp.getStatusDescription());
                        strings.add(transactionTopUp.getTerminalNo() + "");
                        strings.add(transactionTopUp.getTraceNo() + "");



                        break;
                    case SALES:
                        ProtoGlobal.MplTransaction.Sales transactionSales = mplTransaction.getSales();

                        break;
                    case BILL:
                        ProtoGlobal.MplTransaction.Bill transactionBill = mplTransaction.getBill();


                        break;
                }

            }
            adapter.setInfo(strings);
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
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_mplTransActionInfo);
        statusTv = rootView.findViewById(R.id.tv_mplTransactionInfo_stats);
        dataTv = rootView.findViewById(R.id.tv_mplTransactionInfo_data);
        timeTv = rootView.findViewById(R.id.tv_mplTransactionInfo_time);
        statusIv = rootView.findViewById(R.id.fv_mplTransactionInfo_status);
        statusRootView = rootView.findViewById(R.id.cl_mplTransactionInfo_statusRoot);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext());
        toolBarContainer.addView(toolbar.getView());
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

    private void pendingStatus() {
        statusTv.setText("وضعیت \n نامشخص");
        statusTv.setTextColor(getResources().getColor(R.color.narenjitire));
        statusIv.setTextColor(getResources().getColor(R.color.narenjitire));
        statusIv.setText(getResources().getString(R.string.error_icon));
    }
}
